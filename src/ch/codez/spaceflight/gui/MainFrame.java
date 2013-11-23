package ch.codez.spaceflight.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaList;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;
import ch.codez.spaceflight.AppOptions;

public class MainFrame extends JFrame {

    private static Logger log = Logger.getLogger(MainFrame.class);

    private EmbeddedMediaPlayer mediaPlayer;

    private MediaListPlayer mediaListPlayer;

    private FlightAnimation animation;

    public MainFrame() {
        super("Space Flight");
        this.animation = new FlightAnimation(this);
        this.initWindow();
        this.initPlayer();
    }

    public void runFullScreen() {
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setVisible(true);

        if (AppOptions.getInstance().getIsKioskMode()) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            gs.setFullScreenWindow(this);
        }
        this.validate();

        mediaPlayer.enableOverlay(true);
        mediaListPlayer.play();

        animation.setSize(this.getSize());
        animation.start();
    }

    private void initWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.BLACK);
        this.setUndecorated(true);
        this.setResizable(false);
        setLayout(new BorderLayout());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_F11:
                    mediaPlayer.enableOverlay(!mediaPlayer.overlayEnabled());
                    break;

                case KeyEvent.VK_SPACE:
                    mediaPlayer.pause();
                    animation.pause();

                    break;
                }
            }
        });
    }

    private void initPlayer() {
        Canvas vs = new Canvas();
        add(vs, BorderLayout.CENTER);

        MediaPlayerFactory factory = new MediaPlayerFactory("--vout=macosx");

        mediaPlayer = factory.newEmbeddedMediaPlayer();
        mediaPlayer.setVideoSurface(factory.newVideoSurface(vs));
        mediaPlayer.setOverlay(animation);

        mediaListPlayer = factory.newMediaListPlayer();

        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
            @Override
            public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item,
                    String itemMrl) {
                log.info("Next movie is up..");
            }
        });

        mediaListPlayer.setMediaPlayer(mediaPlayer);

        MediaList mediaList = factory.newMediaList();
        File folder = new File(AppOptions.getInstance().getPathMovies());
        List<File> movies = Arrays.asList(folder.listFiles());
        Collections.shuffle(movies);
        for (File f : movies) {
            mediaList.addMedia(f.getAbsolutePath());
        }

        mediaListPlayer.setMediaList(mediaList);
        mediaListPlayer.setMode(MediaListPlayerMode.LOOP);
    }
}
