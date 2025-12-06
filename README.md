TrailBlazer README

Contributors: 

Arti Hajdari: 
Expertise: Leadership, Backend (Python, SQL, APIs, etc.), Diagrams, Documentation
Role: Project Lead, ensured development stayed on track, created diagrams/documentation, assisted with backend development

Nafis Uddin: 
Expertise: Backend (Python, SQL, APIs, etc.)
Role: Backend Developer, ensured full backend logic with APIs and database

Rafi Hossain: 
Expertise: Frontend (Kotlin, JS, HTML/CSS, etc.), Diagrams
Role: Frontend Developer, created UI to match Figma Diagrams

Miadul Haque: 
Expertise: Frontend (Kotlin, JS, HTML/CSS, etc.)
Role: Frontend Developer, styled the UI of the app, ensured functionality between frontend and backend

Ryan King:
Expertise: Frontend (Kotlin, Google Maps, etc.), Diagrams
Role: Frontend Developer, created Map UI with Google Maps, connected frontend and backend for full functionality, assisted with diagrams




* DM Nafis for .env file that holds key for JWT secret, and the later is gonna have the NPS API. I'll email it to you guys.

* DM Ryan for the local.properties file that holds key for google maps API.
* Quick update: We are using kotlin for the frontend and map wiring. Rafi says we can hook it up to the figma which is fire.

* I wired up the api to the app frontend, it should work properly with backend, I just couldn't verify because of no .env for NPS API. -RK

A few pointers in case yall need some help with the frontend:

POST auth/register (email, password, display name) returns access token

POST auth/login returns the same token

GET auth/about returns the user data

GET /trails?near=lat,lon&radius=km -- is a query string for when we want to see list of trails with lat/lon difficulty, length, and average rating

GET /trails/{id} -- gives us the details of a specific trail

GET /trails/{id}/review -- gives us the reviews of the specific trail

POST /trails/{id}/review -- lets us add a review of a trail

GET /trails -- lists all the trail

GET /parks?near=lat,lon&radius=km -- is a list of parks from the NPS API

GET /parks/{id} -- details of a specific park

GET /parks -- lists all the parks

**-------------------BIG UPDATE 12/1/2025-------------------**

POST /trails/{trail_id}/photos -- upload photo for a trail

GET /trails/{trail_id}/photos -- lets us see the photo

GET /trails/{trail_id}/notes -- list user's notes for a trail

POST /trails/{trail_id}/notes -- create a note

PATCH /notes/{note_id} -- update a note

DELETE /notes/{note_id} -- delete a note

POST /trails/{trail_id}/favorite -- toggle a trail to be favorited

GET /me/favorites -- list all the user's favorite trails

POST /admin/nps/refresh -- basically takes the nps script functions to add parks from nps api to db

POST /trails/{trail_id}/activities -- logs activity of a trail for the user

GET /activities/me -- lists the user's activity with some filters

GET profiles/{user_id} -- gives the user's progess with averaged out stats

GET /profiles/me -- returns the user's profile

PATCH /profiles/me -- updates the user's profile

GET /profiles/{user_id} -- get profile by user id

POST /posts -- creata a post

GET /posts -- list posts with optional filters

GET /posts/{posts_id} -- get a post by it's id

PATCH /posts/{posts_id} -- update a post

DELETE /posts/{posts_id} -- delete post

POST /offline/trails/{trail_id} -- Toggle a trail to be saved offline

GET /offline/trails -- list all the trails that are saved to be offline
