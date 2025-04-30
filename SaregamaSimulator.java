import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SaregamaSimulator extends JFrame {

    private final Map<Character, Note> notes = new LinkedHashMap<>();

    private static class Note {
        double frequency;
        String name;
        Note(double frequency, String name) {
            this.frequency = frequency;
            this.name = name;
        }
    }

    public SaregamaSimulator() {

        notes.put('A', new Note(261.63, "Sa"));
        notes.put('S', new Note(293.66, "Re"));
        notes.put('D', new Note(329.63, "Ga"));
        notes.put('F', new Note(349.23, "Ma"));
        notes.put('G', new Note(392.00, "Pa"));
        notes.put('H', new Note(440.00, "Dha"));
        notes.put('J', new Note(493.88, "Ni"));
        notes.put('K', new Note(523.25, "Sa'"));

        setTitle("🎼 Sa Re Ga Ma Harmonium");
        setSize(900, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Use A S D F G H J K or click the buttons to play Sa Re Ga Ma", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        add(label, BorderLayout.NORTH);

        JPanel keysPanel = new JPanel(new GridLayout(1, notes.size(), 10, 10));
        keysPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (Map.Entry<Character, Note> entry : notes.entrySet()) {
            char keyChar = entry.getKey();
            Note note = entry.getValue();
            JButton button = new JButton(note.name + " (" + keyChar + ")");
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setFocusable(false);
            button.addActionListener(e -> {
                highlightKey(button);
                playTone(note.frequency, 300);
            });
            keysPanel.add(button);
        }

        JButton exitButton = new JButton("Exit (L)");
        exitButton.setFocusable(false);
        exitButton.addActionListener(e -> System.exit(0));

        add(keysPanel, BorderLayout.CENTER);
        add(exitButton, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char ch = Character.toUpperCase(e.getKeyChar());
                if (ch == 'L') System.exit(0);
                if (notes.containsKey(ch)) {
                    playTone(notes.get(ch).frequency, 300);
                }
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SaregamaSimulator::new);
    }

    private void playTone(double freq, int durationMs) {
        try {
            float sampleRate = 44100;
            byte[] buf = new byte[1];
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            sdl.start();
            for (int i = 0; i < durationMs * (float) sampleRate / 1000; i++) {
                double angle = i / (sampleRate / freq) * 2.0 * Math.PI;
                buf[0] = (byte) (Math.sin(angle) * 100);
                sdl.write(buf, 0, 1);
            }
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void highlightKey(JButton button) {
        Color original = button.getBackground();
        button.setBackground(Color.CYAN);
        Timer timer = new Timer(150, evt -> button.setBackground(original));
        timer.setRepeats(false);
        timer.start();
    }
}
