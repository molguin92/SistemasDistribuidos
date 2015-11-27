package cc5303.tareas.olguin_manuel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 06-10-15.
 */
public class ClientFrame extends JFrame
{

    // Frame of the client, takes care of registering keystrokes.

    final Board board_f;
    Timer board_update;

    public ClientFrame(Board board, int[] dimensions)
    {
        this.board_f = board;
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setTitle("Ice Climbers");
        setResizable(false);
        setSize(dimensions[0], dimensions[1]);
        setMinimumSize(new Dimension(dimensions[0], dimensions[1]));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = (JPanel) getContentPane();
        content.add(board);

        InputMap input = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFT");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHT");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "JUMP");

        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "STOP");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "STOP");

        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "RESTART");

        content.getActionMap().put("LEFT", new leftAction());
        content.getActionMap().put("RIGHT", new rightAction());
        content.getActionMap().put("JUMP", new jumpAction());
        content.getActionMap().put("STOP", new stopAction());
        content.getActionMap().put("RESTART", new restartAction());

        pack();
        setVisible(true);

        //Update board graphics every 16 ms ~ 60 fps
        this.board_update = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board_f.repaint();
            }
        });

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    board_f.game.leaving(board_f.playerID);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        this.board_update.start();
    }

    class leftAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                board_f.player.moveLeft();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    class rightAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                board_f.player.moveRight();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    class jumpAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                board_f.player.jump();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    class stopAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                board_f.player.stop();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    class restartAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                board_f.player.voteRestart();
                board_f.request_restart = true;
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }
}
