import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class FlappyBirdGame extends JPanel {
    private Bird bird;
    private ArrayList<Pipe> pipes;
    private int score;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int PIPE_SPACING = 300;

    public FlappyBirdGame() {
        bird = new Bird(100, WINDOW_HEIGHT / 2);
        pipes = new ArrayList<>();
        pipes.add(new Pipe(WINDOW_WIDTH, WINDOW_HEIGHT));
        score = 0;

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    bird.jump();
                }
            }
        });

        Timer timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                repaint();
            }
        });
        timer.start();
    }

    private void updateGame() {
        bird.update();
        for (Pipe pipe : pipes) {
            pipe.update();
        }
        checkCollisions();
        removeOffscreenPipes();
        generateNewPipes();
    }

    private void checkCollisions() {
        for (Pipe pipe : pipes) {
            if (pipe.collidesWith(bird) || bird.getY() >= WINDOW_HEIGHT) {
                JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);
                resetGame();
                break;
            }
        }
    }

    private void removeOffscreenPipes() {
        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
                score++;
            }
        }
    }

    private void generateNewPipes() {
        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).getX() < WINDOW_WIDTH - PIPE_SPACING) {
            pipes.add(new Pipe(WINDOW_WIDTH, WINDOW_HEIGHT));
        }
    }

    private void resetGame() {
        bird = new Bird(100, WINDOW_HEIGHT / 2);
        pipes.clear();
        pipes.add(new Pipe(WINDOW_WIDTH, WINDOW_HEIGHT));
        score = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        bird.draw(g);
        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }
        g.drawString("Score: " + score, 10, 20);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird - Java");
        FlappyBirdGame gamePanel = new FlappyBirdGame();
        frame.add(gamePanel);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Bird {
    private int x, y;
    private int velocity;
    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = -10;

    public Bird(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.velocity = 0;
    }

    public void jump() {
        velocity = JUMP_STRENGTH;
    }

    public void update() {
        velocity += GRAVITY;
        y += velocity;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, 30, 30);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return 30;
    }
}

class Pipe {
    private int x, y;
    private int width, height;
    private int gap;
    private int speed = 5;

    public Pipe(int startX, int windowHeight) {
        this.x = startX;
        this.width = 50;
        this.gap = 150;
        Random rand = new Random();
        this.y = rand.nextInt(windowHeight - gap - 100) + 50;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, 0, width, y);
        g.fillRect(x, y + gap, width, 600 - (y + gap));
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public boolean collidesWith(Bird bird) {
        if (bird.getX() + bird.getSize() > x && bird.getX() < x + width) {
            return bird.getY() < y || bird.getY() + bird.getSize() > y + gap;
        }
        return false;
    }
}
