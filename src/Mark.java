public enum Mark {
    BLANK, X, O;

    @Override
    public String toString() {
        switch (this) {
            case X: return "X";
            case O: return "O";
            case BLANK: return null;
            default: return null;
        }
    }
}