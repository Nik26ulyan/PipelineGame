package ru.vsu.cs.uliyanov_n_s.game;

public class PipelineGameParams {
    private String levelName;

    public PipelineGameParams(String levelName) {
        this.levelName = levelName;
    }

    public PipelineGameParams() {
        this("exampleLevel");
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelName() {
        return levelName;
    }
}
