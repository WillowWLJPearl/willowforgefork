WillowForge / NeoForge (Windows portable)

1. Download WillowForge-Windows.zip from:
   https://github.com/WillowWLJPearl/willowforgefork/releases/latest
2. Extract the entire ZIP to a normal folder (not directly into Program Files).
3. Double-click WillowForge\Play.cmd. Do not run Play.cmd inside the ZIP.
4. In the game: Online -> Host, or Online -> Join with your friend's IP:port.

Each launch checks for a new WillowForge release, updates it when available,
then starts the game. If offline, the current installed copy launches.
No Java or IntelliJ installation required.

All player data, preferences, custom decks and downloaded images are stored in
WillowForge\userdata. Do not delete that folder when moving the game.

Debug.cmd runs the same game with a visible console if Play.cmd does not start.

For internet play: the host needs to be reachable. Enable UPnP in the game's
Host menu if your router supports it, or forward the displayed port manually.
If your ISP uses CGNAT, ask them for a public IP or use a private VPN.
Only share your IP and port with trusted players.

NeoForge warns about version mismatches. Have everyone launch Play.cmd online
before a match so that they all get the same release and custom rules.

For maintainers: push changes to master. GitHub Actions builds a new Windows
portable ZIP and publishes a GitHub Release automatically on success.
