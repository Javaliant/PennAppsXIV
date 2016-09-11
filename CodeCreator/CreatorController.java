/* Author: Luigi Vincent
*
* Controller class for  Code Creator.
*
* 
*/

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class CreatorController {
	@FXML
	private ProgressBar progressBar;
	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField phoneField;
	@FXML
	private TextField itemField;
	@FXML
	private ComboBox<String> providerBox;
	@FXML
	private ComboBox<String> privacyBox;

	private Stage stage;

	@FXML
	private Label notificationLabel;

	private static final String FOLDER_NAME = "Created Codes";
	private static final String FILE_TYPE = "png";
	private static final int CODE_SIZE = 250;

	@FXML
	public void initialize() {
		ensureOutpoutPath();

		providerBox.getItems().addAll(
			"Alltell",
			"AT&T",
			"Boost Mobile",
			"Cricket Wireless",
			"Project Fi",
			"Republic Wireless",
			"Sprint",
			"T-mobile",
			"U.S. Cellular",
			"Verizon",
			"Virgin Mobile"
		);
		privacyBox.getItems().addAll("Yes", "No");
	}

	private void ensureOutpoutPath() {
		try {
			Files.createDirectories(Paths.get(FOLDER_NAME));
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	private void disclose(String message, int seconds) {
		notificationLabel.setText(message);
		new Timeline(new KeyFrame(
			Duration.seconds(seconds),
			ae -> notificationLabel.setText("")))
		.play();
	}

	private void generateCodes(File csv) {
		Task<Void> generatorTask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				try (Scanner file = new Scanner(csv)) {
					while (file.hasNextLine()) {
						String codeText = file.nextLine();
						String filepath = FOLDER_NAME + '/' + codeText.replaceAll(",", "_") + '.' + FILE_TYPE;
						codeText = Encryptor.encode(codeText);
						File generatedFile = new File(filepath);

						try {
							Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
							hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
							
							// Possible to change border size (white border size to just 1)
							hintMap.put(EncodeHintType.MARGIN, 1); // default = 4
							hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				 
							QRCodeWriter qrCodeWriter = new QRCodeWriter();
							BitMatrix byteMatrix = qrCodeWriter.encode(codeText, BarcodeFormat.QR_CODE, CODE_SIZE, CODE_SIZE, hintMap);
							int width = byteMatrix.getWidth();
							BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
							image.createGraphics();
				 
							Graphics2D graphics = (Graphics2D) image.getGraphics();
							graphics.setColor(Color.WHITE);
							graphics.fillRect(0, 0, width, width);
							graphics.setColor(Color.BLACK);
				 
							for (int k = 0; k < width; k++) {
								updateProgress(k, width); //* (size / i));
								for (int j = 0; j < width; j++) {
									if (byteMatrix.get(k, j)) {
										graphics.fillRect(k, j, 1, 1);
									}
								}
							}

							ImageIO.write(image, FILE_TYPE, generatedFile);
							//updateProgress(i + 1, size);
							updateProgress(1, 1);
						} catch (WriterException | IOException e) {
							e.printStackTrace();
						}
					}
				} catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
				}

				Platform.runLater(() -> disclose("QR code(s) successfully generated", 5));
				return null;
			}
		};
		new Thread(generatorTask).start();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void generate() {
		Task<Void> generatorTask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				updateProgress(0, 100);
				progressBar.setCursor(Cursor.WAIT);
				
				String codeText = nameField.getText()
					+ ',' + emailField.getText()
					+ ',' + phoneField.getText()
					+ ',' + providerBox.getValue().toString()
					+ ',' + itemField.getText()
					+ ',' + providerBox.getValue().toString()
					+ ',' + (privacyBox.getValue().toString().equals("Yes") ? "n" : "y");
				String filepath = FOLDER_NAME + '/' + codeText.replaceAll(",", "_") + '.' + FILE_TYPE;
				codeText = Encryptor.encode(codeText);
				File generatedFile = new File(filepath);

				try {
					Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
					hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
					
					// Possible to change border size (white border size to just 1)
					hintMap.put(EncodeHintType.MARGIN, 1); // default = 4
					hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		 
					QRCodeWriter qrCodeWriter = new QRCodeWriter();
					BitMatrix byteMatrix = qrCodeWriter.encode(codeText, BarcodeFormat.QR_CODE, CODE_SIZE, CODE_SIZE, hintMap);
					int width = byteMatrix.getWidth();
					BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
					image.createGraphics();
		 
					Graphics2D graphics = (Graphics2D) image.getGraphics();
					graphics.setColor(Color.WHITE);
					graphics.fillRect(0, 0, width, width);
					graphics.setColor(Color.BLACK);
		 
					for (int k = 0; k < width; k++) {
						updateProgress(k, width); //* (size / i));
						for (int j = 0; j < width; j++) {
							if (byteMatrix.get(k, j)) {
								graphics.fillRect(k, j, 1, 1);
							}
						}
					}

					ImageIO.write(image, FILE_TYPE, generatedFile);
					//updateProgress(i + 1, size);
					updateProgress(1, 1);
				} catch (WriterException | IOException e) {
					e.printStackTrace();
				}
				
				progressBar.setCursor(Cursor.DEFAULT);
				Platform.runLater(() -> disclose("QR code(s) successfully generated", 5));
				return null;
			}
		};

		progressBar.progressProperty().bind(generatorTask.progressProperty());
		new Thread(generatorTask).start();
	}

	public void generateFromCSV() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose CSV");
		fileChooser.getExtensionFilters().addAll(
			new ExtensionFilter("CSV Files", "*.csv"),
			new ExtensionFilter("All Files", "*.*")
		);
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			generateCodes(selectedFile); 
		}
	}

	public void openFolder() {
		try {
			Desktop.getDesktop().open(new File("Created Codes"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}