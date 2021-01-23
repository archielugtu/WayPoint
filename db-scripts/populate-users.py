#!/usr/bin/env python3

"""
Python script used to populate the database with random user data

All the users created by this script will have password of "password"
"""

import requests
import sys

s = requests.Session()

URL = "https://randomuser.me/api/?results={}&nat=nz"
#ROOT_URL = "https://csse-s302g2.canterbury.ac.nz/prod/api"
ROOT_URL = "http://localhost:9499"
SIGNUP_URL = f"{ROOT_URL}/profiles"
LOGIN_URL = f"{ROOT_URL}/login"
ACTIVITY_CREATE_URL = "{ROOT_URL}/profiles/{user_id}/activities"
ACTIVITY_ROLE_URL = "{ROOT_URL}/profiles/{user_id}/subscriptions/activities/{activity_id}"
PASSWORD_HASHED = "4081F4658E97108C"

def retrieve_data(url):
    """
    Retrieves data from URL provided returns the JSON
    """
    return s.get(url).json()["results"]


def parse_single_user(single_user):
    """
    Reads data from for a single user and return their
    fields

    The fields returned are
    - firstname
    - lastname
    - email
    - city
    - state
    - country
    - gender
    - date of birth

    Password is set to "password"
    """
    user_data = {
        "firstname": single_user["name"]["first"],
        "lastname": single_user["name"]["last"],
        "middlename": "",
        "nickname": "",
        "bio": "",
        "primary_email": single_user["email"],
        "additional_email": [],
        "password": PASSWORD_HASHED,
        "date_of_birth": single_user["dob"]["date"][:10],
        "gender": single_user["gender"].capitalize(),
        "fitness": 2,
        "passports": [single_user["location"]["country"]],
        "activities": [],
        "location": {
            "city": single_user["location"]["city"],
            "state": single_user["location"]["state"],
            "country": single_user["location"]["country"],
        },
    }

    return user_data


def signup_with_user(user):
    """
    Sends a POST request to the backend in order
    to sign up with user data specified
    """
    print("Signup up user")
    resp = s.post(SIGNUP_URL, json=user)
    print(f"Response status: {resp.status_code}")
    if resp.status_code != 200:
        return (None, None)
    return (resp.json()["id"], user["primary_email"])


def login(email):
    """
    Sends a POST request to the backend to login with email specified
    """
    print(f"Logging in with email: {email}")
    resp = s.post(LOGIN_URL, json={"email": email, "password": PASSWORD_HASHED})
    print(f"Response status: {resp.status_code}")
    return resp.json()["userId"]


def create_activity(creator_id):
    """
    Sends a POST request to the backend to create a
    random activity
    """
    print("Creating a random activity")
    random_user_data = retrieve_data(URL.format(1))[0]
    fname = random_user_data["name"]["first"]
    lname = random_user_data["name"]["last"]
    location = random_user_data["location"]["city"]
    activity_name = f"Hang out with {fname} {lname}"
    activity_json = {
        "activity_type": ["Hang Gliding experience"],
        "activity_name": activity_name,
        "description": "",
        "continuous": True,
        "start_time": "",
        "end_time": "",
        "location": location,
        "hashtags": [],
        "specifications": [],
        "visibility": "public"
    }
    resp = s.post(ACTIVITY_CREATE_URL.format(
        ROOT_URL=ROOT_URL,
        user_id=creator_id), json=activity_json)
    print(f"Response status: {resp.status_code}")
    if (resp.status_code != 201):
        print(f"Response status: {resp.json()}")
        sys.exit(1)

    return resp.text


def add_engagement_to_activity(user_id, activity_id, role="follower"):
    """
    Sends a POST request to the backend to add a role
    """
    print(f"Adding user with ID {user_id} with engagement '{role}' to activity {activity_id}")
    assert role in ["follower", "participant", "organiser"]
    resp = s.post(
        ACTIVITY_ROLE_URL.format(
            ROOT_URL=ROOT_URL, user_id=user_id, activity_id=activity_id
        ),
        json={"role": role},
    )
    print(f"Response from server: {resp.status_code}")


def main():
    creator_email = input("Which *email* should I create the activity under? ")
    num_users = int(input("How many users should I create for *each* role? "))

    roles = ["follower", "participant", "organiser"]

    creator_id = login(creator_email)
    activity_id = create_activity(creator_id)
    random_users_data = retrieve_data(URL.format(num_users * len(roles)))

    for index, role in enumerate(roles):
        start_index = index * num_users
        end_index = index * num_users + num_users
        user_creds = []
        for data in random_users_data[start_index:end_index]:
            user_creds.append(signup_with_user(parse_single_user(data)))

        if role == "organiser":
            login(creator_email)

        for user_id, email in user_creds:
            if not user_id:
                continue
            if role != "organiser":
                login(email)
            add_engagement_to_activity(user_id, activity_id, role=role)

    print("Activity can be accessed through this URL: ")
    print(f"{ROOT_URL}/activities/{activity_id}")

if __name__ == "__main__":
    main()
