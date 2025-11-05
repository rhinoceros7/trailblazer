"""
Script to import parks with the NPS API.

example is: python3 -m scripts.import_nps NY       or can be NH FOR MY BOY RYAN KING

then after it gave a result: NPS import for NY: {'inserted': 32, 'updated': 0, 'total': 32} -- ran it for the first time so it freshly added 32
then we can check if it is in the tables by doing: sqlite3 trailblazer.db ".tables" 
which gave us: parks    reviews  trails   users -- these are the tables we have in models.py
then we do: sqlite3 trailblazer.db "SELECT id, name, state, lat, lon FROM parks LIMIT 5;"
and we got 5 parks back:
1|Quick Park|NY|40.78|-73.97
2|Sample Park|NY|40.78|-73.97
3|African Burial Ground|NY|40.71452681|-74.00447358
4|Appalachian|NY|40.41029575|-76.4337548
5|Captain John Smith Chesapeake|NY|38.971601|-76.483355
"""


import sys
from app.db import Base, engine, SessionLocal
from app.services.nps import import_parks_by_states


def main():
    if len(sys.argv) < 2: # has to be 2 characters for the State code
        print("Make sure it is a valid state code, ex: NY, NH, NJ")
        sys.exit(1)

    state_code = sys.argv[1].upper() # make sure it is uppercase (e.g NY not ny)

    Base.metadata.create_all(bind=engine) # have our tables

    db = SessionLocal()
    try:
        result = import_parks_by_states(db, state_code)
        print(f"NPS import for {state_code}: {result}")
    finally:
        db.close()


if __name__ == "__main__":
    main()
