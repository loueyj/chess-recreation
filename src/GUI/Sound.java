package GUI;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.net.URL;

public class Sound {

    Clip musicClip;
    URL[] url = new URL[10];

    public Sound() {

        url[0] = getClass().getResource("/sounds/move-self.wav");
        url[1] = getClass().getResource("/sounds/move-check.wav");
        url[2] = getClass().getResource("/sounds/capture.wav");
        url[3] = getClass().getResource("/sounds/castle.wav");
        url[4] = getClass().getResource("/sounds/illegal.wav");
        url[5] = getClass().getResource("/sounds/promote.wav");

    }

    public void play(int i, boolean music) {

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();

            if (music) {
                // If the passed through sound is music and not a sound effect then pass it to musicClip to be used in other methods
                musicClip = clip;
            }

            clip.open(ais);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close(); // Need to close the clip to avoid computer memory overuse
                }
            });
            ais.close();
            clip.start();
        } catch (Exception e) {
            throw new IllegalArgumentException("");
        }
    }

    // Methods for playing music
    public void loop() {
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop() {
        musicClip.stop();
        musicClip.close();
    }
}
