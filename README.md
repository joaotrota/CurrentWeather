# CurrentWeather

## Architectural Decisions

- The implementation of the project follows Clean Architecture principles. In the Domain we have CurrentWeatherInteractor which is responsible for the management of all the current weather use cases. This interactor is implemented in a reactive way, as it exposes a stream of Weather objects which is the domain entity. Whenever there was any action that changed the state of the domain entity the interactor emits a new entity.

- Clean Architecture alongside TDD allowed me to implement most of the application without having to implement any UI. I started by implementing the domain layer, by faking the data layer implementations. Then I proceeded to the data and presentation layers, again faking any dependencies needed and finally in the last commit UI was implemented.  

- The chosen presentation design pattern was MVP since it fulfills the application's needs.

## Technology and Framework decisions

- Decided to use only one Activity since this was a one screen application.
- WorkManager was used since it fits very well with the use case of periodic weather fetching.
- Retrofit was not used since there was really no need for it! Pure OkHttp was used instead!
- Chose not to use any library to perform JSON parsing since, again, there was no need to use it. If i had the need to pick one i would choose Moshi

## Package structure

The project was implemented with a "feature per package" strategy. The **currentweather** package contains all the layers of the feature: UI, Presentation, Domain and Data.

## Testing strategy

The implementation was done using TDD and it contains 3 types of tests across all the layers:
  - Unit tests which test the isolated behaviour of each small component.
  - Integration tests which were used to test the integration of WorkManager. The data layer was also tested with integration tests: I used both Robolectric and Mockwebserver to perform these tests.
  - Acceptance tests, which are large integration tests that test the various acceptance criteria of the feature. These type of tests are close to end-to-end tests, with the difference being that these tests don't use real networking. These are inspired on what Martin Fowler calls Component Tests: https://martinfowler.com/bliki/ComponentTest.html
  
Another adopted strategy was to limit the use of Mocks and Spies in favor of Fakes as a design strategy to help keep the tests as decoupled of the implementation as possible. 

### Dependency Injection

Since this was a very small project i decided to implement manual dependency injection. But if the project were to scale certainly it would need to be refactored to use a better DI tool, like Dagger.


### Possible improvements

- Constants like the base url and API key are created in the WeatherApplication class. Even though this was not a worry for a project of this size, it could be improved by extracting these constants for example to resources. 
- Improve the way permissions are handled. Right now it keeps asking for permissions until the user accepts!
- Since there is no subscription to location updates, if the Location Service doesn't have a last known location we will not be able to obtain it. This could be improved by subscribing to location updates!
- Improve the UI. Since there was no need for a fancy UI i simply did not implement one, but if the app was to go into production this would be needed.
- There is some duplicated code in CurrentWeatherInteractor and WeatherUpdateWorker and perhaps this could be refactor by extracting this logic into another object and having both those classes using it a dependency. Since the project is covered with acceptance tests this would be a safe and easy refactor to do.
