* DM Nafis for .env file that holds key for JWT secret, and the later is gonna have the NPS API. I'll email it to you guys.

* DM Ryan for the local.properties file that holds key for google maps API.
* Quick update: We are using kotlin for the frontend and map wiring. Rafi says we can hook it up to the figma which is fire.

A few pointers in case yall need some help with the frontend:

POST auth/register (email, password, display name) returns access token

POST auth/login returns the same token

GET auth/about returns the user data

GET /trails?near=lat,lon&radius=km -- is a query string for when we want to see list of trails with lat/lon difficulty, length, and average rating

GET /trails/{id} -- gives us the details of a specific trail

GET /trails/{id}/review -- gives us the reviews of the specific trail

GET /parks?near=lat,lon&radius=km -- is a list of parks from the NPS API

GET /parks/{id} -- detials of a specific park