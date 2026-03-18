Create a spring boot based web app with ui to demonstrate idempotent API


## Backend

### Service Methods

- Create  Service called User Service and a Corresponding entity User
- Create 4 methods

    1) create(user)
    returns the user passed
    2) createIdem(user)
    - hashes the input object/json
    - checks if the hash is present in redis cache
    - if present, return ERROR object already created
    - else return the user passed
    - set the TTL for redis as 30 seconds
    3) createWithKey(user, key)
    - checks if the key is present in redis cache
    - if present, return ERROR object already created
    - else return the user passed
    - set the TTL for redis as 30 seconds
    4) createAsync(user, key)
    - schedule a job using quartz scheduler which executes in 10 seconds
    - set the key as the key for the job, such that , job with same key should not be executed before the execution of the current job completes.
    - the create API is called inside the job execution and response is returned
    - once the job is complete , it sends a websocket notification to the front end 


### APIs
Create 5 APIs and one service components
POST /users/create - calls a create(user)
POST /users/create-idem - calls createIdem(user) 
POST /users/create-with-key - createWithKey(user, key)
POST /users/create-async - createAsync(user, key)

for APIs other than create , use a new Class IdemUser with this schema
{
    idemKey : string
    userData : {
        "name" : string,
        "address" : string,
        "phone" : integer
    }
}

for API create , use 
{
    "name" : string,
    "address" : string,
    "phone" : integer
}


## Front End
The screen should have 2 sections one below the other
### Top section
- A text area with a user JSON
- A drop down with 4 options
a) Create  - calls /create
b) Create with Idempotency - calls /idem-create
c) Create with Idempotent Key - calls /create-with-key
d) Create Async with Key - calls /create-async
- A Text area to hold a JSON string
- A button to call the APIs respectively 

Botton section should have a flowwing log of API response, that updates every time the button is click. Bottom section also listens to a websocket to receive response ,and append to log