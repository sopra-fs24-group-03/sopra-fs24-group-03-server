# SoPra Group 3 Project: Poker

## Introduction
Welcome to our project; A free, user-friendly online poker game. This game allows friends to connect, compete, and enjoy poker without the distractions and limitations of other online alternatives.

Our whole team is a big fan of the game of poker. Therefore, it was clear we wanted to create a poker game. But what would make our poker game special? We wanted to create a game where friends could play poker together, whenever and wherever they are. Despite the plethora of online options, finding a satisfactory platform to play poker with friends can be difficult. Most online poker games are cluttered with ads, require in-app purchases, or are just not user friendly.

By eliminating ads and unnecessary costs, we ensure that our platform is accessible to everyone, making it easy to start a game of poker and practice some new strategies or just have fun with your friends.

The Poker variation we implemented is Texas hold'em; the rules can be read [here](https://en.wikipedia.org/wiki/Texas_hold_'em#Rules), any implementation-specific alterations are documented on the frontpage of our website.

## Technologies used:

The Server has been written in Java using the following technologies:
- **Spring Boot**: External API calls to the server are handled by the Spring Boot framework.
- **Java Persistence API (JPA)**: Data persistence is managed using JPA, allowing entities to be saved in an in-memory database.
- **Deck of Cards API**: The logic for the card decks is managed by the external Deck of Cards API, enabling us to create new decks and draw cards from those decks through API requests.

## High-Level Components

### UserService
The [`UserService`](src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java) class handles requests related to user management, including:
- Creating new users
- Logging in and logging out
- Displaying user information

The [`User`](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/User.java) entity is persisted using the JPA database.

### LobbyService
The [`LobbyService`](src/main/java/ch/uzh/ifi/hase/soprafs24/service/LobbyService.java) class manages requests associated with lobby operations, such as:
- Creating a lobby
- Joining or leaving a lobby
- Kicking a user from a lobby
- Retrieving lobby information
- Allowing users to start a game

Note that when a game is started, it is initialized not with the User entity directly, but with Player entities, where the relevant information is copied over from the user entity.

The [`Lobby`](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Lobby.java) is also saved as an entity inside the JPA database.

### GameService
The [`GameService`](src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java) class handles the core game logic, including:
- Allowing users to make moves and updating the game state accordingly
- Updating user information when a game is finished
- Deleting the game from the JPA after a short period once it is finished
- Retrieving game information

When a game is finished, the game updates the user entities with the new values. After that, it waits for 10 seconds and deletes the Game, Player, and GameTable entities from the JPA.

The information relevant to the game is saved inside three separate entities in the JPA:
- The [`Game`](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Game.java) entity saves the state the game is in and references the other two entities.
- The [`Player`](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Player.java) entity saves the relevant information for each player.
- The [`GameTable`](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/GameTable.java) entity saves the relevant information of the table.

All of these are persisted in the JPA.

## Launch & Deployment

To launch and deploy the server, follow these steps:

1. **Install Dependencies**: Dependencies are managed using Gradle. It is however not required to have Gradle installed on your system. To install dependencies with the Gradle wrapper, run the following command:
    ```
    ./gradlew build
    ```

2. **Run the Server Locally**: To run the server locally, execute the following command:
    ```
    ./gradlew bootRun
    ```

3. **Testing**: To run tests, use the following command:
    ```
    ./gradlew test
    ```

4. **Deployment to Google Cloud with GitHub Actions**: The server is deployed to Google Cloud using GitHub Actions. Ensure your GitHub repository is set up with appropriate GitHub Actions workflows for deployment. Push your changes to the repository, and the deployment workflow will automatically build and deploy the server to Google Cloud.


## Roadmap

Following are some feature ideas that could be implemented:

- **Retry Buy Back**: Allow users to buy back retries, giving them something to do with their money.
- **Matchmaking**: A matchmaking system allowing users to join a lobby with random people. Additionally, allow users to set their lobby as private so that others can't join through matchmaking.
- **Global Leaderboard**: Display a leaderboard of all users, sorted by how many retries a user needed and their money.

## Authors and Acknowledgements

### Authors

- Joel Huber - Backend and Frontend - [joelhube](https://github.com/joelhube)
- Cèdric Huber - Backend and Frontend - [cedihube](https://github.com/cedihuber)
- Linus Lautenschlager - Frontend - [Lilololl](https://github.com/Lilololl)
- Noah Ziegler - Backend - [N-oahh](https://github.com/N-oahh)
- Colin Bächtold - Backend - [Colbae](https://github.com/Colbae)

### Acknowledgements

We would like to acknowledge the following for their contributions and support:

- [**Spring Boot Team**](https://spring.io/team): For providing the framework to send and receive API calls and the JPA to create in-memory databases.
- [**SoPra Team**](https://github.com/HASEL-UZH/sopra-fs24-template-server): For providing us with a template to build our server on.

Special thanks to:
- [**Chase Roberts**](https://github.com/crobertsbmw/): For creating the [Deck of Cards API](https://www.deckofcardsapi.com/) that we used to create decks and draw cards from those decks.


## License

This project is licensed under the GNU General Public License - see the [LICENSE](LICENSE) file for details
