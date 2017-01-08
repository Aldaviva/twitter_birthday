twitter_birthday
================

Update the birthday on your Twitter profile so you always get balloons.

When you run this program, it will modify your Twitter profile so the birthday is the current month and day, with the year
you specify. Use cron or Task Scheduler to see balloons every day.

Once you change your profile birthday a lot, Twitter's desktop web page will tell you that "You can only change your 
profile a limited number of times", and once you change it even more, you won't be able to select different values. 
Luckily, this is a UI-only restriction. This program will continue to work even after Twitter's web UI prevents further 
birthday updates.

## Requirements

- Java &ge; 7
- Maven 3

## Installation

```bash
git clone https://github.com/Aldaviva/twitter_birthday.git
cd twitter_birthday
mvn package
```

This creates the executable JAR file `./target/twitter_birthday.jar`.
    
## Configuration

In the same directory as `twitter_birthday.jar`, create a subdirectory named `twitter_birthday_conf`.

    target
    │   twitter_birthday.jar
    └── twitter_birthday_conf/
        |   config.properties

Inside this new subdirectory, create a new text file named `config.properties` with these keys and example values.

    twitter.authToken=abcdefg
    twitter.username=dril
    twitter.birthdayVisibility.day=PUBLIC
    twitter.birthdayVisibility.year=PUBLIC
    birthYear=1984
    
### Configuration values

Change the values in `config.properties`.
    
- **birthYear:** The year you want to appear in your Twitter profile's birthday. Required.
- **twitter.authToken:** Log in to Twitter with your browser and steal the `auth_token` cookie. Required.
- **twitter.username:** Your Twitter handle, without the leading `@`. Required.
- **twitter.birthdayVisibility.day:** Who should be able to see your birthday's month and day. Can be one of
    - `PUBLIC`
    - `MY_FOLLOWERS`
    - `PEOPLE_I_FOLLOW`
    - `WE_FOLLOW_EACH_OTHER`
    - `ONLY_ME`
    
    Optional, defaults to `WE_FOLLOW_EACH_OTHER` if you omit this property.
    
- **twitter.birthdayVisibility.year:** Who should be able to see your birthday's year. Same possible values as 
`twitter.birthdayVisibility.day`. Optional, default value is `ONLY_ME`.
- **twitter.baseUri:** Alternate server URI for accessing Twitter. Optional, defaults to `https://twitter.com`.

## Running

```bash
java -jar twitter_birthday.jar
```
<!-- -->
```bash
01:15:35.466 [main] INFO  c.a.t.t.auth.TwitterAuthServiceImpl - Starting new Twitter session for user dril
01:15:36.260 [main] INFO  c.a.t.t.s.TwitterBirthdayUpdaterImpl - Updating birthday to January 8, 1984
01:15:36.398 [main] INFO  c.a.t.t.s.TwitterBirthdayUpdaterImpl - Successfully updated birthday on Twitter profile to January 8, 1984
```