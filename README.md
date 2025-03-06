#### Some information on development in this repository

(Ég nennti ekki að skrifa þetta á íslensku, afsakið!!)

## This repository has three main packages:
  - Models
  - Services
  - UI

The *Models* package contains our data classes that our json responses are parsed into. These data classes can be used by both *Services* and *UI*. The Models package should only include the models we need in order to represent our entities. So, if it is not represented by our database schema, it should probably not be a data class.

The *Services* package should contain all our service classes. These classes are a layer between the HttpsService - which directly communicates with the API - and the *UI* pakage. All business logic belongs in the *Service* package. The HttpsService class is only responsible for directly communicating with the API, the rest is down to the specific service classes. If we find that we are copy pasting code from one service to another, consider a subdirectory for utility classes where we can combine logic that is shared across services. Service class methods should recieve a https response object and return a data class in **most** cases, can also be a boolean or something if the fragment that is using the service does not need the data object. 

The *UI* package contains all of our fragments. It might be more comfortable for us to split this into smaller modules as the project grows, if we are creating the third fragment beginning with "Courses" for example, we might want to seperate those fragments into their own folder so it is easier for us to navigate. Working from the advice of our TA, we currently only have one activity in our ui, the MainActivity. From there, we use Fragments to switch between views when a user selects a new "view". This has been pretty comfortable to do so far, so we will stick to only one activity unless we feel we need another. This might be good later for the bigger components, such as Assignments, Courses, etc, we will see.

## Naming Conventions:

camelCase

## Docs:

We should try our best to document methods, this will make it much easier for us to step into each others code. Like this:
/**
 * This function returns the sum of two integers.
 *
 * @param a the first integer
 * @param b the second integer
 * @return the sum of a and b
 */

## Tests:

 We should also try to write some unit tests for our **service classes**, specifically methods that return a data class as it takes much longer to system test than unit test. 
 I will merge some examples of this soon!

## Merge Conflicts:

 Make sure not to stage or commit unversioned files when adding and committing, as this will cause conflicts and we want these files to stay ignored by git. 
 You can pull, fetch and merge or rebase when getting the latest changes from master to your branch. You can also use the UI in Android Studio to do this. Sometimes we can use the github UI to solve the conflicts as well if a pull request has been made. Generally, it's good to not make sure your branch is up to data before starting again if the branch has been inactive. Post to the discord or tag someone in the pull request if there are major conflicts and you are unsure which changes to keep. 
 
## Pull Requests:

 When ready to merge, push and create a pull request and ask someone to review it. Beware that when you merge your branch into master, the working branch you were on will be deleted. If you go to Projects -> Hugbúnaðarverkefni 2 LearningSquare -> Current Iteration you will find the tasks that we need to be woking on now. This task list is not exhaustive, but includes all user stories from the Assignment 1 sprints we turned in. If you click a task you can navigate to development under the task and click Create a branch for this issue or link a pull request to create a branch that is connected to this task. This means you don't have to move your tasks to Done when they are done, because github will already know, neat! If you've already created a branch, you can also link a pull request to the issue. 

## The Endpoints:

 I am still working on fixing the naming of the endpoint as well as some logic in our backend. We also need to host our database somewhere as sqlite writes to disk and we will never be able to store any data for longer than the instance of our backend is running (which is like 10-15 minutes). I am working on this and will update when the new databse is ready and all endpoints in our backend have been upgraded. 

## Good to know:

 If the app is crashing unexpectedly during login or signup, it is likely that the API request is timing out. To avoid this, make sure the instance of the backend is running by calling a simple GET endpoint or something before running the app. This can be done via curl or postman. It can take the server 50+ seconds to start if it has been idle.

