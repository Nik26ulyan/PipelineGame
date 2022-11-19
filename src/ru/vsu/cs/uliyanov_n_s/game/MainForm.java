package ru.vsu.cs.uliyanov_n_s.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ru.vsu.cs.util.DrawUtils;
import ru.vsu.cs.util.JTableUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private JTable tableGameField;
    private JLabel labelStatus;
    private JLabel labelTime;

    private static final String EXAMPLE_LEVEL_NAME = "exampleLevel";

    private static final int DEFAULT_GAP = 8;
    private static final int DEFAULT_CELL_SIZE = 70;


    private PipelineGameParams params = new PipelineGameParams(EXAMPLE_LEVEL_NAME);
    private Game game = new Game();
    private ParamsDialog dialogParams;

    private final Timer timer = new Timer(1000, e -> {
        if (game.getTimeLeft() > -1) {
            labelTime.setText("Осталось времени: " + game.getTimeLeft());
        } else {
            game.calculateGameState();
        }
        updateView();
    });


    public MainForm() {
        this.setTitle("Сокобан");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(400, 100);
        this.pack();

        setJMenuBar(createMenuBar());
        this.pack();

        SwingUtils.setShowMessageDefaultErrorHandler();

        tableGameField.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(tableGameField, DEFAULT_CELL_SIZE, false, false, false, false);
        tableGameField.setIntercellSpacing(new Dimension(0, 0));
        tableGameField.setEnabled(false);

        tableGameField.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    int width = getWidth() - 2;
                    int height = getHeight() - 2;
                    paintCell(row, column, g2d, width, height);
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });

        newGame();

        updateWindowSize();
        updateView();

        dialogParams = new ParamsDialog(params, tableGameField, e -> newGame());

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    game.movePlayer(-1, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    game.movePlayer(1, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    game.movePlayer(0, -1);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    game.movePlayer(0, 1);
                }
                updateView();
            }
        });
    }

    private JMenuItem createMenuItem(String text, String shortcut, Character mnemonic, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        if (shortcut != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.replace('+', ' ')));
        }
        if (mnemonic != null) {
            menuItem.setMnemonic(mnemonic);
        }
        return menuItem;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBarMain = new JMenuBar();

        JMenu menuGame = new JMenu("Игра");
        menuBarMain.add(menuGame);
        menuGame.add(createMenuItem("Новая", "ctrl+N", null, e -> {
            newGame();
        }));
        menuGame.add(createMenuItem("Меню", "ctrl+P", null, e -> {
            dialogParams.updateView();
            dialogParams.setVisible(true);
        }));
        menuGame.addSeparator();
        menuGame.add(createMenuItem("Выход", "ctrl+X", null, e -> {
            System.exit(0);
        }));

        JMenu menuView = new JMenu("Вид");
        menuBarMain.add(menuView);
        menuView.add(createMenuItem("Изменить размер окна", null, null, e -> {
            updateWindowSize();
        }));
        menuView.addSeparator();
        SwingUtils.initLookAndFeelMenu(menuView);

        JMenu menuHelp = new JMenu("Справка");
        menuBarMain.add(menuHelp);
        menuHelp.add(createMenuItem("Правила", "ctrl+R", null, e -> {
            SwingUtils.showInfoMessageBox("Трубопровод - это игра, в которой игроку нужно создать работающий санузел." +
                            "На уровне находятся объекты, требующие системы подключения и отвода воды. Цель игрока - за ограниченное время создать такую систему, используя ограниченное пространство и набор труб",
                    "Правила");
        }));
        menuHelp.add(createMenuItem("О программе", "ctrl+A", null, e -> {
            SwingUtils.showInfoMessageBox(
                    "Игра Трубопровод на ЯП Java" +
                            "\n\nАвторы: Ульянов Н." +
                            "О программе"
            );
        }));

        return menuBarMain;
    }

    private void updateWindowSize() {
        int menuSize = this.getJMenuBar() != null ? this.getJMenuBar().getHeight() : 0;
        SwingUtils.setFixedSize(
                this,
                tableGameField.getWidth() + 2 * DEFAULT_GAP + 25,
                tableGameField.getHeight() + panelMain.getY() + labelStatus.getHeight() +
                        menuSize + DEFAULT_GAP + 2 * DEFAULT_GAP + 60
        );
        this.setMaximumSize(null);
        this.setMinimumSize(null);
    }

    private void updateView() {
        tableGameField.repaint();
        if (game.getState() == Game.GameState.PLAYING) {
            labelStatus.setForeground(Color.BLACK);
            labelStatus.setText("Игра идет");
        } else {
            timer.stop();
            labelStatus.setText("");
            if (game.getState() == Game.GameState.WIN) {
                labelStatus.setForeground(Color.RED);
                labelStatus.setText("Победа :-)");
                dialogParams.updateView();
                dialogParams.setVisible(true);
            } else if (game.getState() == Game.GameState.FAIL) {
                labelStatus.setForeground(Color.RED);
                labelStatus.setText("Поражение :-(");
                dialogParams.setVisible(true);
            }
        }
    }


    private Font font = null;

    private Font getFont(int size) {
        if (font == null || font.getSize() != size) {
            font = new Font("Comic Sans MS", Font.BOLD, size);
        }
        return font;
    }

    private void paintCell(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        Game.PipelineCell cell = game.getCell(row, column);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(cellWidth, cellHeight);

        Color backColor = Color.LIGHT_GRAY;
        if (cell.getState() == Game.CellState.WALL) {
            backColor = new Color(189, 32, 32);
        } else if (cell.getState() == Game.CellState.EMPTY) {
            backColor = new Color(80, 80, 80);
        } else if (cell.getState() == Game.CellState.PLAYER) {
            backColor = new Color(80, 80, 80);
        } else if (cell.getState() == Game.CellState.WRONG_TUBE) {
            backColor = Color.BLACK;
        } else if (cell.getState() == Game.CellState.PLACE_FOR_TUBE) {
            backColor = new Color(80, 80, 80);
        } else if (cell.getState() == Game.CellState.RIGHT_TUBE) {
            backColor = Color.BLUE;
        }
        g2d.setColor(backColor);
        g2d.fillRect(0, 0, size, size);

        Color color = DrawUtils.getContrastColor(backColor);
        String texture = " ";
        int sizeMod = 1;
        if (game.getState() == Game.GameState.FAIL && cell.getState() == Game.CellState.PLAYER) {
            texture = "*o*";
            sizeMod = 3;
        } else {
            if (cell.getState() == Game.CellState.WALL) {
                texture = "#";
                color = new Color(243, 225, 225);
            } else if (cell.getState() == Game.CellState.WRONG_TUBE || cell.getState() == Game.CellState.RIGHT_TUBE) {
                texture = "==";
                sizeMod = 2;
            } else if (cell.getState() == Game.CellState.PLAYER) {
                texture = "::";
                sizeMod = 1;
                color = new Color(62, 171, 0);
            } else if (cell.getState() == Game.CellState.PLACE_FOR_TUBE) {
                texture = "()";
                sizeMod = 2;
                color = new Color(62, 171, 0);
            }
        }
        g2d.setColor(color);
        int bound = (int) Math.round(size * 0.1);
        Font font = getFont(size - (2 * sizeMod) * bound);
        DrawUtils.drawStringInCenter(g2d, font, texture, 0, 0, cellWidth, (int) Math.round(cellHeight * 0.95));
    }

    private void newGame() {
        game.newGame(params.getLevelName());
        JTableUtils.resizeJTable(tableGameField,
                game.getRowCount(), game.getColCount(),
                tableGameField.getRowHeight(), tableGameField.getRowHeight()
        );
        timer.start();
        updateView();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(3, 3, new Insets(10, 10, 10, 10), -1, 10));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelMain.add(scrollPane1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableGameField = new JTable();
        scrollPane1.setViewportView(tableGameField);
        labelStatus = new JLabel();
        labelStatus.setText("Label");
        panelMain.add(labelStatus, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelTime = new JLabel();
        labelTime.setText("Label");
        panelMain.add(labelTime, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
