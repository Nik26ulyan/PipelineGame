package ru.vsu.cs.uliyanov_n_s.game;

import ru.vsu.cs.util.ArrayUtils;

public class LevelLoader {
    public Game.PipelineCell[][] loadLevelFromFile(String fileName) {
        String[][] levelStringArray = ArrayUtils.readStringArray2FromFile("levels/" + fileName + ".txt");
        assert levelStringArray != null;
        return stringArrayToLevelField(levelStringArray);
    }

    private Game.PipelineCell[][] stringArrayToLevelField(String[][] levelStringArray) {
        Game.PipelineCell[][] field = new Game.PipelineCell[levelStringArray.length][levelStringArray[0].length];
        for (int r = 0; r < field.length; r++) {
            for (int c = 0; c < field[r].length; c++) {
                switch (levelStringArray[r][c]) {
                    case "w":
                        field[r][c] = new Game.PipelineCell(Game.CellState.WALL);
                        break;
                    case "e":
                        field[r][c] = new Game.PipelineCell(Game.CellState.EMPTY);
                        break;
                    case "t":
                        field[r][c] = new Game.PipelineCell(Game.CellState.WRONG_TUBE);
                        break;
                    case "s":
                        field[r][c] = new Game.PipelineCell(Game.CellState.PLACE_FOR_TUBE);
                        break;
                    case "r":
                        field[r][c] = new Game.PipelineCell(Game.CellState.RIGHT_TUBE);
                        break;
                    case "p":
                        field[r][c] = new Game.PipelineCell(Game.CellState.PLAYER);
                        break;
                    default:
                        field[r][c] = new Game.PipelineCell(Game.CellState.EMPTY);
                }
            }
        }
        return field;

    }
}
