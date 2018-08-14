![DockerizedCraft](/docs/logo-small.png)

Dockerized Craft - Core
======================

__Never maintain your Bungeecord manually again! Automatically listens to Docker events and adds servers to BungeeCord.__

Additionally supporting plugin messaging, connection balancing and proxy server list updates.

![DockerizedCraft Preview](/docs/container-manager-demo.gif)

## Container Inspector

### Features

- Automatically listens on docker events
- Extracts environment variables from containers
- Triggers BungeeCord Custom events on Docker container events

### Configuration

Check [container-inspector.yml](/src/main/resources/container-inspector.yml)

## Server Updater

### Features

- Automatically adds Server to Bungeecord
- Automatically removes Server to Bungeecord
- Configurable add/remove actions
- Supports Health Checks

### Configuration

Check [server-updater.yml](/src/main/resources/server-updater.yml)

## Connection Balancer

### Features

- Server Groups
- Default connection group (Fallback/Default servers)
- Forced hosts for groups (i.e. eu lobbies and us lobbies)
- Listens on Container events to add servers to groups based on their environment variables (i.e SERVER_GROUP=lobby)
- Connection Balancing Strategies per group
  - balanced: Connect Players to the Server of the group with the fewest players
  - More will follow!
- Does not overwrite restrictions!


### Configuration

Check [connection-balancer.yml](/src/main/resources/connection-balancer.yml)


## Plugin Notifier

### Server List Payload

Send Server information to the single servers.
This can be used to add Sever Selectors etc.

Add as many information as you want by easy to use environment variable mapping.

__Example Payload__ (Channel: ContainerManager, Subchannel: ServerData)

````json
{
   "us-lobby-1":{
      "address":"172.19.0.5:25565",
      "host":"172.19.0.5",
      "port":25565,
      "motd":"A Minecraft Server Instance",
      "name":"us-lobby-1",
      "proxied_players":12,
      "type":"spigot",
      "category":"us-lobby",
      "tags":"some,awesome,tags"
   },
   "eu-lobby-2":{
      "address":"172.19.0.4:25565",
      "host":"172.19.0.4",
      "port":25565,
      "motd":"A Minecraft Server Instance",
      "name":"eu-lobby-2",
      "proxied_players":5,
      "type":"spigot",
      "category":"eu-lobby",
      "tags":""
   },
   "eu-lobby-1":{
      "address":"172.19.0.3:25565",
      "host":"172.19.0.3",
      "port":25565,
      "motd":"A Minecraft Server Instance",
      "name":"eu-lobby-1",
      "proxied_players":0,
      "type":"spigot",
      "category":"eu-lobby",
      "tags":""
   }
}
````

### Configuration

Check [plugin-notifier.yml](/src/main/resources/plugin-notifier.yml)

## Try it yourself

1. Checkout the repository
2. Build the .jar with maven or copy an attached one from the last releases.
3. run `docker-compose --project-name minecraft up -d`
4. Wait until all containers did start and connect to localhost with you Minecraft client

## Todo's

- Reduce .jar size