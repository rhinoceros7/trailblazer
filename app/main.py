"""
Main for Trailblazer

- Creates the FastAPI app
- Ensures database tables exist
- Mounts feature routers for trails and auth
- static files for photos
"""

import os

from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles  # where we have images
from fastapi.middleware.cors import CORSMiddleware  # Allow mobile app to call API


from app.db import engine, Base  # SQLAlchemy engine + declarative Base
from app.routers import trails, auth, parks, notes, favorites, nps_admin, activities, profiles, posts, offline # Feature router for all endpoints

#App initialization

# Create the FastAPI application object
# The title/version show up in the auto generated docs at /docs
app = FastAPI(title="Trailblazer", version="0.1.0")

# Create all database tables on startup
Base.metadata.create_all(bind=engine)


# CORS so React Native app can talk to this API during development


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Routers
# Trails router which has:
# GET /trails/ which list trails
# GET /trails/{trail_id} which gets one trail
# POST /trails/{trail_id}/reviews which adds a review to a trail

# We also have the Authentication: 
# POST /auth/register which lets the user create an account
# POST /auth/login which lets the user log into the account
# added some more but will write out later ^^^^

app.include_router(auth.router, trails.router, parks.router, notes.router, notes.router, favorites.router, nps_admin.router, activities.router, profiles.router, posts.router, offline.router)



# Static files for uploading images
# static files directory will be /static.
# will create a static folder under apps folder to handle the photos
app.mount("/static", StaticFiles(directory="app/static"), name="static")


os.makedirs("media", exist_ok=True) # media directory has to exist for the photos
app.mount("/media", StaticFiles(directory="media"), name="media")


# check to verify the API is up
@app.get("/")
def health():
    return {"status": "ok", "service": "trailblazer"}


"""
Still have to add:
- parks
- more features for trails like has viewpoint and whatnot
- photos
- personal notes
- lists of favorites
- community (follow(ers/ing), prolly a social feed too)
- activity & progress
- search
- NPS API: which will hold more parks/trails and import wtv else we think would be nice to have from it




keeping track of features:
profiles - DONE
community - DONE
favorites - DONE
progress - DONE
notes - DONE
offline download - DONE
nps pipeline to insert trails to db - DONE
search/filters
"""