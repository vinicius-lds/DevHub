package br.com.proway.util;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * @author Vinícius Luis da Silva
 */
public abstract class GridPaneUtil {

    public static Node getNode(GridPane gridPane, int column, int row) {
        for (Node n : gridPane.getChildren()) {
            if (GridPane.getRowIndex(n) == row && GridPane.getColumnIndex(n) == column) {
                return n;
            }
        }
        return null;
    }

    public static int getRowCount(GridPane gridPane) {
        int numRows = gridPane.getRowConstraints().size();
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Node child = gridPane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }
        return numRows;
    }

    public static void deleteRow(GridPane grid, final int row) {
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : grid.getChildren()) {
            // get index from child
            Integer rowIndex = GridPane.getRowIndex(child);

            // handle null values for index=0
            int r = rowIndex == null ? 0 : rowIndex;

            if (r > row) {
                // decrement rows for rows after the deleted row
                GridPane.setRowIndex(child, r - 1);
            } else if (r == row) {
                // collect matching rows for deletion
                deleteNodes.add(child);
            }
        }

        // remove nodes from row
        grid.getChildren().removeAll(deleteNodes);
    }

}
