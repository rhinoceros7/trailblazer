"""
Authentication:
- POST /auth/register: create a user (hashed password)
- POST /auth/login: verify credentials, return JWT
"""
from datetime import datetime, timedelta, timezone
from typing import Optional

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, EmailStr, Field
from passlib.hash import bcrypt
import jwt

from sqlalchemy.orm import Session

from app.db import SessionLocal
from app.models import User
from app.config import settings
from app.callback import get_db, get_current_user

router = APIRouter(prefix="/auth", tags=["auth"])



class RegisterIn(BaseModel):
    email: EmailStr
    password: str = Field(min_length=6)
    display_name: str = Field(min_length=1, max_length=100)


class LoginIn(BaseModel):
    email: EmailStr
    password: str


class TokenOut(BaseModel):
    access_token: str
    token_type: str = "bearer" # this maps back to the get_current_user in callback.py which we use to show we are getting a user


class UserOut(BaseModel): # user can access this information
    id: int
    email: EmailStr
    display_name: str
    time_created: datetime

    class Config: # allows to fetch the information
        from_attribute = True


class PasswordChange(BaseModel):
    current_password: str = Field(min_length=6, max_length=100)
    new_password: str = Field(min_length=6, max_length=100)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def create_access_token(*, user_id: int) -> str:
    now = datetime.now(tz=timezone.utc)
    exp = now + timedelta(minutes=settings.JWT_EXPIRE_MINUTES)
    payload = {
        "sub": str(user_id), # subject = UserID
        "iat": int(now.timestamp()), # issued at = time when created
        "exp": int(exp.timestamp()), # expiration time
    }
    return jwt.encode(payload, settings.JWT_SECRET, algorithm=settings.JWT_ALG) # boom, our secret sauce is created.



@router.post("/register", response_model=TokenOut)
def register(data: RegisterIn, db: Session = Depends(get_db)):
    # ensure unique email
    if db.query(User).filter(User.email == data.email).first():
        raise HTTPException(status_code=400, detail="Email already registered")

    user = User(
        email=data.email,
        password_hash=bcrypt.hash(data.password),
        display_name=data.display_name,
    )
    db.add(user)
    db.commit()
    db.refresh(user)

    token = create_access_token(user_id=user.id)
    return TokenOut(access_token=token)


@router.post("/login", response_model=TokenOut)
def login(data: LoginIn, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == data.email).first()
    if not user or not bcrypt.verify(data.password, user.password_hash): # if email not found or invalid password
        raise HTTPException(status_code=401, detail="Invalid credentials")
    token = create_access_token(user_id=user.id)
    return TokenOut(access_token=token)


@router.post("/about", response_model=UserOut) # fetches user information
def about(current_user: User = Depends(get_current_user)):
    return current_user

@router.post("/change-password", status_code=status.HTTP_204_NO_CONTENT) # no content = user isnt getting data back since we are changing password
def change_password(
    payload: PasswordChange,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if not bcrypt.verify(payload.current_password, current_user.password_hash): # remember, the user is changing their password bc they want to, not bc they forgot it. 
        raise HTTPException(status_code=400, detail="Current password is incorrect") # first, they need to provide their current password to be able to change it to a new one.
    
    current_user.password_hash = bcrypt.hash(payload.new_password)
    db.add(current_user)
    db.commit()
    return


@router.post("/refresh", response_model=TokenOut)
def refresh(current_user: User = Depends(get_current_user)): # user token still has to be active.
    token = create_access_token(user_id=current_user.id) # user gets a fresh JWT and gets to stay in the same session without having to log in again.
    return TokenOut(access_token=token)
