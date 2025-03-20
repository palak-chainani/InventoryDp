import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

// Base class for Inventory Control (Abstraction)
abstract class InventoryControl {
    protected double[] demand; // Demand over multiple periods
    protected double holdingCost;
    protected double orderCost;
    protected Map<Integer, Double> memo; // DP memoization

    // Constructor to initialize values (Encapsulation)
    public InventoryControl(double[] demand, double holdingCost, double orderCost) {
        this.demand = demand;
        this.holdingCost = holdingCost;
        this.orderCost = orderCost;
        this.memo = new HashMap<>();
    }

    // Abstract method to be implemented by child classes
    public abstract String calculate();
}

// Subclass for EOQ with Dynamic Programming (Inheritance, Polymorphism)
class EconomicOrderQuantityDP extends InventoryControl {

    // Constructor using parent constructor
    public EconomicOrderQuantityDP(double[] demand, double holdingCost, double orderCost) {
        super(demand, holdingCost, orderCost);
    }

    // Method to calculate EOQ with DP (Overriding calculate method)
    @Override
    public String calculate() {
        double optimalCost = dp(0); // Start at the first period
        return "Economic Order Quantity : " + String.format("%.2f", optimalCost) + " units.";
    }

    // DP method to calculate EOQ over time
    private double dp(int period) {
        // Base case: If we have processed all periods
        if (period >= demand.length) {
            return 0;
        }

        // Check if result is already computed
        if (memo.containsKey(period)) {
            return memo.get(period);
        }

        double totalDemand = 0;
        double minCost = Double.MAX_VALUE;

        // Try ordering at each future period and calculate cost
        for (int nextPeriod = period; nextPeriod < demand.length; nextPeriod++) {
            totalDemand += demand[nextPeriod];

            // Calculate the cost for ordering at this point
            double orderQuantity = totalDemand;
            double orderCostForThisPeriod = orderCost; // Fixed order cost
            double holdingCostForThisPeriod = totalDemand * holdingCost * (nextPeriod - period + 1);

            // Cost for this decision + cost for future periods
            double totalCost = orderCostForThisPeriod + holdingCostForThisPeriod + dp(nextPeriod + 1);

            // Take the minimum cost over all periods
            minCost = Math.min(minCost, totalCost);
        }

        // Memoize the result
        memo.put(period, minCost);
        return minCost;
    }
}

// GUI Class (Encapsulation of GUI components and logic)
public class InventoryControlGUIDP extends Frame {

    // GUI Components
    private TextField demandField, holdingCostField, orderCostField, periodsField;
    private TextArea resultArea;
    private Button calculateEOQButton;

    public InventoryControlGUIDP() {
        // Setup Frame properties
        setTitle("Inventory Control System with DP");
        setSize(600, 400);
        setLayout(new BorderLayout()); // Use BorderLayout for main layout

        // Panel for input fields (using GridLayout for alignment)
        Panel inputPanel = new Panel(new GridLayout(4, 2, 10, 10));

        // Initialize GUI components
        Label demandLabel = new Label("Demand per Period (comma-separated): ");
        demandField = new TextField();

        Label holdingCostLabel = new Label("Holding Cost per Unit per Year: ");
        holdingCostField = new TextField();

        Label orderCostLabel = new Label("Order Cost (per order): ");
        orderCostField = new TextField();

        Label periodsLabel = new Label("Number of Periods: ");
        periodsField = new TextField();

        // Add input components to the panel
        inputPanel.add(demandLabel);
        inputPanel.add(demandField);
        inputPanel.add(holdingCostLabel);
        inputPanel.add(holdingCostField);
        inputPanel.add(orderCostLabel);
        inputPanel.add(orderCostField);
        inputPanel.add(periodsLabel);
        inputPanel.add(periodsField);

        // Add the input panel to the Frame
        add(inputPanel, BorderLayout.NORTH);

        // Panel for the button (using FlowLayout to center the button)
        Panel buttonPanel = new Panel(new FlowLayout());
        calculateEOQButton = new Button("Calculate EOQ");
        buttonPanel.add(calculateEOQButton);
        add(buttonPanel, BorderLayout.CENTER);

        // Result area for displaying output (using BorderLayout for full width at the bottom)
        resultArea = new TextArea();
        resultArea.setEditable(false);
        add(resultArea, BorderLayout.SOUTH);

        // Action listener for EOQ calculation
        calculateEOQButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateEOQ();
            }
        });

        // Add Window Closing Event
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    // Method to calculate EOQ using Dynamic Programming
    private void calculateEOQ() {
        try {
            // Input parsing
            String[] demandStr = demandField.getText().split(",");
            double[] demand = new double[demandStr.length];
            for (int i = 0; i < demandStr.length; i++) {
                demand[i] = Double.parseDouble(demandStr[i]);
            }

            double holdingCost = Double.parseDouble(holdingCostField.getText());
            double orderCost = Double.parseDouble(orderCostField.getText());

            // Create EOQ object and calculate using DP
            EconomicOrderQuantityDP eoqDP = new EconomicOrderQuantityDP(demand, holdingCost, orderCost);
            resultArea.setText(eoqDP.calculate());
        } catch (NumberFormatException ex) {
            resultArea.setText("Invalid input! Please enter numeric values.");
        }
    }

    public static void main(String[] args) {
        // Create and display the GUI
        InventoryControlGUIDP inventoryControlGUIDP = new InventoryControlGUIDP();
        inventoryControlGUIDP.setVisible(true);
    }
}