evenbettermusicplayer
=====================

It is a fork of the Pretty Good Music Player and allows to use this great app on newer phones.

Feature Details:
- Audiobook mode for resuming playback where you left off
- Notification controls
- The color theme and text size are now configurable!
- This player protects against accidental pausing when your phone is jostled and the headphone cord shifts. I added this because it was annoying when my phone was in my pocket and would auto-pause because the headphone cable was bumped.
- Plays .mp3, .m4p, .m4a, .aac, and many more types of audio files.
- Accepts play, pause, previous, and next commands from Bluetooth headsets.
- When you select 'All songs' for a given artist, the songs are ordered by album, not alphabetically. 
- Automatically pauses and resumes when another app (e.g. GPS) needs audio.
- If another app needs audio for more than 30 seconds (e.g. a phone call), it does not automatically resume. 
- "Repeat All" is always on.
- If you click "back" too quickly, it double checks that you want to leave before quitting.

Expected Folder Layout:
It's designed to work with artist folders copied from iTunes. That means it expects directory structure:

music/
artist/
/album1
/01 - First Song.mp3
/02 - Second Song.mp3
/03 - Third Song.mp3
/04 - Fourth Song.mp3
/album2
/01 - Another Song.mp3
....
