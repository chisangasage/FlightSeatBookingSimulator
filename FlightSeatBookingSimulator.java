import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class FlightSeatBookingSimulator extends JFrame {
    private static final int ROWS = 25;
    private static final int COLUMNS = 6;
    private JButton[][] seats = new JButton[ROWS][COLUMNS];
    private Connection connection;

    public FlightSeatBookingSimulator() {
        // Database connection
        connectToDatabase();

        setTitle("Flight Seat Booking Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel seatPanel = new JPanel();
    // Adjust grid layout to fix column alignment (ROWS + 1 for labels, COLUMNS + 2 for walkway and row labels)
    seatPanel.setLayout(new GridLayout(ROWS + 1, COLUMNS + 2)); // +2 to account for walkway and row labels

    // Add the column labels (A, B, C, Walkway, D, E, F)
    String[] labels = {"A", "B", "C", "D", "E", "F"}; 
    seatPanel.add(new JLabel("")); // Empty top-left corner for alignment
    for (int col = 0; col < COLUMNS; col++) {
        if (col == 3) {
            seatPanel.add(new JLabel("")); // Invisible label for the walkway
        }
        seatPanel.add(new JLabel(labels[col < 3 ? col : col - 1], SwingConstants.CENTER)); // Column labels
    }

    // Initialize seats and row labels
    for (int row = 0; row < ROWS; row++) {
        seatPanel.add(new JLabel(String.valueOf(row + 1), SwingConstants.CENTER)); // Row labels
        for (int col = 0; col < COLUMNS + 1; col++) {
            if (col == 3) {
                seatPanel.add(new JLabel("")); // Walkway column (no seat here)
                continue;
            }
            JButton seat = new JButton();
            seat.setBackground(Color.GRAY); // Empty seat
            seat.addActionListener(new SeatActionListener(row, col < 3 ? col : col - 1)); // Adjust column index after walkway
            seats[row][col < 3 ? col : col - 1] = seat; // Adjust column index for skipped walkway
            seatPanel.add(seat);

            // Load seat status from the database
            loadSeatStatus(row, col < 3 ? col : col - 1); // Adjust column for skipped walkway
        }
    }


        // Add the seat panel to the frame
        add(seatPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton clearSelectedButton = new JButton("Clear Selected");
        clearSelectedButton.addActionListener(e -> clearSelectedSeats()); 
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> clearAllSeats()); 
        buttonPanel.add(clearSelectedButton);
        buttonPanel.add(clearAllButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    // Action listener for seat buttons
    private class SeatActionListener implements ActionListener {
        private int row;
        private int col;

        public SeatActionListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton seat = seats[row][col];
            if (seat.getBackground() == Color.GRAY) { // Empty seat
                seat.setBackground(Color.GREEN); // Mark as selected
            } else if (seat.getBackground() == Color.GREEN) { // Selected seat
                bookSeat(row, col); // Book the seat in the database
                seat.setBackground(Color.RED); // Mark as booked
                seat.setEnabled(false); // Disable booking
            }
        }
    }

    // Book a seat and update the database
    private void bookSeat(int row, int col) {
        try {
            String query = "INSERT INTO seats (seat_row, seat_column, status) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE status = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, row + 1);
            stmt.setString(2, getColumnLabel(col));
            stmt.setString(3, "booked");
            stmt.setString(4, "booked");
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clear selected (green) seats but not booked (red)
    private void clearSelectedSeats() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (col == 3) { // Skip the walkway (index 3)
                    continue;
                }
                if (seats[row][col] != null && seats[row][col].getBackground() == Color.GREEN) {
                    seats[row][col].setBackground(Color.GRAY); // Reset to empty
                }
            }
        }
    }

    // Clear all seats (resets everything)
    private void clearAllSeats() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (col == 3) { // Skip the walkway (index 3)
                    continue;
                }
                if (seats[row][col] != null) {
                    seats[row][col].setBackground(Color.GRAY); // Reset to empty
                    seats[row][col].setEnabled(true); // Re-enable booking
                }
            }
        }
        clearDatabase();
    }


    // Load seat status from the database
    private void loadSeatStatus(int row, int col) {
        try {
            String query = "SELECT status FROM seats WHERE seat_row = ? AND seat_column = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, row + 1);
            stmt.setString(2, getColumnLabel(col));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if ("booked".equals(status)) {
                    seats[row][col].setBackground(Color.RED);
                    seats[row][col].setEnabled(false);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clear the database (for demonstration purposes)
    private void clearDatabase() {
        try {
            String query = "DELETE FROM seats";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Connect to the MySQL database
    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/seat_booking";
            String user = "root"; // Your MySQL username
            String password = ""; // Your MySQL password
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Get the seat column label (A, B, C, D, E, F)
    private String getColumnLabel(int col) {
        String[] labels = {"A", "B", "C", "D", "E", "F"};
        if (col < 3) return labels[col];
        else return labels[col - 1]; // Skip the walkway
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightSeatBookingSimulator());
    }
}
