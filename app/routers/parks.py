"""
Parks API router

Endpoints:
- GET /parks and filter with:
- near="lat,lon"
- radius=km
- limit, offset

GET /parks/{park_id}

Notes:
Uses haversine again for nearby parks
"""

from typing import Generator, List, Optional
from math import radians, sin, cos, asin, sqrt

from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session


from app.db import SessionLocal
from app.models import Park
from app import schemas


router = APIRouter(prefix="/parks", tags=["parks"])


def get_db() -> Generator[Session, None, None]:
    # Create a new SQLAlchemy Session for this request and then close it after
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# Haversine formula in KM for nearby filtering -- gets nearby between TWO points
def haversine_km(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
   # Compute great circle distance between two (lat,lon) points in kilometers.
    R = 6371.0 # this is earths radius
    dlat = radians(lat2 - lat1)
    dlon = radians(lon2 - lon1)
    a = sin(dlat / 2) ** 2 + cos(radians(lat1)) * cos(radians(lat2)) * sin(dlon / 2) ** 2
    return 2 * R * asin(sqrt(a))



@router.get("/", response_model=List[schemas.ParkOut])
# response model is how our output is given from the schemas, i.e. after a link that is / -- the information of parks is outputted with the data we defined in schema for Park Out
def list_parks(
    near: Optional[str] = Query(
        default=None,
        description="Comma-separated 'lat,lon' to filter by nearby (e.g., '40.758,-73.9855').",
        examples=["40.758,-73.9855"],
    ), # description and examples dont actually run anything -- it shows up in the docs for the localhost
    radius: float = Query(
        default=50,
        ge=0.1,
        le=200,
        description="Search radius in kilometers when 'near' is provided (0.1–200).",
    ),
    limit: int = Query(
        default=100,
        ge=1,
        le=200,
        description="Maximum number of parks to return.",
    ),
    offset: int = Query(
        default=0,
        ge=0,
        description="Number of parks to skip.",
    ),
    db: Session = Depends(get_db),
):

    q = db.query(Park)

    if not near:
        return q.offset(offset).limit(limit).all()

    # Parse near="lat,lon"
    try:
        lat_s, lon_s = map(float, near.split(",")) # break up into the two different points of lat/lon
    except Exception:
        raise HTTPException(
            status_code=400,detail="Invalid 'near' format. Use 'lat,lon' (e.g., '40.758,-73.9855').")

    # only considers parks that have coordinates
    q = q.filter(Park.lat.isnot(None), Park.lon.isnot(None))

    parks = q.all()
    results: list[Park] = []
    for p in parks:
        d_km = haversine_km(lat_s, lon_s, float(p.lat), float(p.lon)) # run the haversine formula to find nearby
        if d_km <= radius: # only give results within a certain radius
            results.append(p)

    # sort by name
    results_sorted = sorted(results, key=lambda p: p.name)

    return results_sorted[offset : offset + limit] # chunks up the number of parks to be shown


@router.get("/{park_id}", response_model=schemas.ParkOut)
def get_park(park_id: int, db: Session = Depends(get_db)):
    park = db.get(Park, park_id) # gets a park by its id
    if not park:
        raise HTTPException(status_code=404, detail="Park not found")
    return park
