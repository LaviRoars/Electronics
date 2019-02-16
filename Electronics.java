import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// for pane
import javafx.scene.layout.BorderPane;	// BorderPane
import javafx.scene.layout.GridPane;	// GridPane
import javafx.scene.layout.FlowPane;
import javafx.geometry.Insets;		// for padding

import javafx.scene.layout.Priority;

// For components such as Label, Button, Textfields
import javafx.scene.control.*;

//Event
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

//db
import java.sql.*;

public class Electronics extends Application implements EventHandler<ActionEvent>{
	private TextField input;
	private TextArea output;

	public void handle(ActionEvent e){
		Button b = null;
		if(e.getSource() instanceof Button)
			b = (Button)e.getSource();

		if(b!= null && b.getText().equals("Clear")){
			input.setText("");
			output.setText("");

		}
		else{
			try {
				Class.forName("com.mysql.jdbc.Driver");

				Connection connection =
					DriverManager.getConnection("jdbc:mysql://127.0.0.1/bank","comps311","comps311");

				String sql1 = "select brand, price, stock from Item where model = ?";
				PreparedStatement pl = connection.prepareStatement(sql1);
				pl.setString(1, input.getText().trim()); //insert into sql selection criteria for Item model

				ResultSet rs1 = pl.executeQuery();

				if(rs1.next()){
					StringBuffer sb = new StringBuffer();
					//get results for brand, price, stock
					String brand = rs1.getString("brand");
					int price = rs1.getInt("price");
					int stock = rs1.getInt("stock");

					//display results output
					sb.append(String.format("Brand: %s, Price: %d, Stock: %d"));

					//get results for list of suppliers
					String sql2 = "select s.name, soi.supplierprice, s.supplierid, s.telephoneno, s.address from supplier s, supplierofitem soi where s.supplierid=soi.supplierid and soi.model=?";
					PreparedStatement p2 = connection.prepareStatement(sql2);
					p2.setString(1, input.getText().trim()); //insert into sql selection criteria for Item Model

					ResultSet rs2 = p2.executeQuery();


					while(rs2.next()){
						String sName = rs2.getString("name");
						int sPrice = rs2.getInt("supplierprice");
						String sID = rs2.getString("supplierid");
						String sTel = rs2.getString("telephoneno");
						String sAdd = rs2.getString("address");

						sb.append(String.format("%s\t%8d\t%s\t%s\t\t%s\n",sName, sPrice, sID, sTel, sAdd));
					}

					output.setText(sb.toString()); //set output into TextArea
				}
				else{
					output.setText("Product not found");

				}

			}
			catch(SQLException sqle){
				sqle.printStackTrace();
			}
			catch(ClassNotFoundException cnfe){
				cnfe.printStackTrace();

			}

		}

	}

	public void start(Stage primaryStage){

		this.input = new TextField(); //textfield for user input
		input.setOnAction(this);	//Output will be displayed when user hits Enter

		this.output = new TextArea();
		output.setEditable(false);	//Users are not allowed to edit output area

		GridPane bottomPane = new GridPane();

		Button clearBtn = new Button("Clear");
		clearBtn.setOnAction(this);
		clearBtn.setMaxWidth(Double.MAX_VALUE);
		clearBtn.setMaxHeight(Double.MAX_VALUE);
		GridPane.setHgrow(clearBtn, javafx.scene.layout.Priority.ALWAYS);
		GridPane.setVgrow(clearBtn, javafx.scene.layout.Priority.ALWAYS);

		Button submitBtn = new Button("Submit");
		submitBtn.setOnAction(this);
		submitBtn.setMaxWidth(Double.MAX_VALUE);
		submitBtn.setMaxHeight(Double.MAX_VALUE);
		GridPane.setHgrow(submitBtn, javafx.scene.layout.Priority.ALWAYS);
		GridPane.setVgrow(submitBtn, javafx.scene.layout.Priority.ALWAYS);

		bottomPane.add(clearBtn, 0, 0);
		bottomPane.add(submitBtn, 1, 0);

		BorderPane pane = new BorderPane();
		pane.setTop(input);
		pane.setCenter(output);
		pane.setBottom(bottomPane);

		Scene scene = new Scene(pane, 500, 300);

		primaryStage.setTitle("Electronics");

		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String args[]) {
		Application.launch(args);
	}

}