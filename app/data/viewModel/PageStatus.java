package data.viewModel;

public class PageStatus {

    public PageStatus(boolean visited, boolean valid) {

        this.visited = visited;
        this.valid = valid;
    }

    public boolean isVisited() {

        return visited;
    }

    public boolean isValid() {

        return valid;
    }

    private boolean visited;
    private boolean valid;
}
