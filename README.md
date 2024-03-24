# SocialSync (Unit 7)

## Overview

### Description

It is hard to schedule times to meet when you are a college student, everyone in the friends group has conflicting schedules. We are coming with an App where users can schedule time to meet by selecting the times they are available. Our app is a mobile version of the https://www.when2meet.com/ website, it also shows the weather information on the day of meeting.

### App Evaluation

**Category** - Social

**Mobile** - Our app is a mobile version of the WhenToMeet website, we are making it mobile by using GPS location services for finding the weather, push notifications for updates and remainders, sound effects for notifications and deletion of events, we are also using firebase's real time database so the changes are instant

**Story** - The main motivation behind this app is to make scheduling events easy for college students, college students often have very different schedules and it is hard for them to plan and go for events, this is where our app excels, users can choose the days and the time they are free and the event creator can choose a time when all people are free, our app also shows the weather data so that the events can be scheduled without any inconvenience.

**Market**

#### Market size and uniqueness

Our app enters the market as a unique solution by combining the when2meet functionality integrated with real time weather data, so users can decide the type of the meeting/event either indoor or outdoors.

#### Size and Scale of potential user base

The potential user base of our app can extend to millions, we believe that our app will be used by students and working professionals to schedule study sessions and work meetings.

#### Value to Niche group

SocialSync uniquely caters to college students and working professionals by simplifying the coordination of meetups despite hectic schedules, integrating real-time weather data for informed planning, and ensuring instant updates through a mobile platform, making it an helpful tool for developing community and connectivity.

**Habit**

This app is made for college students and working professionals, they will need to schedule meetings very often, so we believe people are likely to use our app at least once evey week.

The app has a great balance between consumption and creation, users not only consume information through updates from events and weather forecasts they also actively create by scheduling their available times and creating events. This dual interaction ensures that the interaction is continuous.

**Scope**

#### Technical Challenges

It can be challenging to finish this app, we are planning to use Google Authentication and we will use firebase for that, the GPS and weather data can be retrieved using the respective APIs, we are also using firebase's realtime database for real time updates.

The real challenges are desigining the UI, designing the database schema so that users can create events and others can join them or update the availability and also implementing the link share feature. We want users to share an event information as a link so that other users can update their availability.

## Product spec

### 1. User Features

#### Required Features

1. Users can signup/login with their gmail accounts(Google Auth)
2. Users can create events, each event has a name, location, options and an optional password.
    - Options can set the type of an event like the event creator can choose to make the event a chatroom or inperson, he can also make other users see the participants and make the event public.
3. Event creators can select and choose the available times and also the days, either by selecting the days of a week in general or specific dates.
4. Event creators can share an event link to their friends and contacts, and the people who received the link can input their times and access the event information by opening the link.
5. Users have a navigation bar at the bottom of the screen which has 3 options, dashboard, events and profile
6. On the dashboard screen users can see the current date, day and time and a recycler view of their upcoming events.
7. On the events screen users can see their public events
8. 
