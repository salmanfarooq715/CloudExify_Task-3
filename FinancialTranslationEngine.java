import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class FinancialTranslationEngine extends Application {

    //------------------------------
    // UI Controls
    //------------------------------

    private ComboBox<String> sourceCurrency;
    private ComboBox<String> targetCurrency;

    private TextField amountField;

    private Button convertButton;
    private Button resetButton;
    private Button swapButton;
    private Button copyButton;

    private Label resultLabel;
    private Label rateLabel;
    private Label statusLabel;
    private Label timeLabel;

    private ProgressIndicator loader;

    private TableView<HistoryRecord> historyTable;

    private ObservableList<HistoryRecord> history =
            FXCollections.observableArrayList();

    private DecimalFormat df = new DecimalFormat("#,##0.00");



    //-----------------------------------
    // 30 Supported Currencies
    //-----------------------------------

    private final String[] currencies = {

            "USD",
            "EUR",
            "GBP",
            "PKR",
            "INR",
            "JPY",
            "AUD",
            "CAD",
            "CHF",
            "CNY",

            "AED",
            "SAR",
            "QAR",
            "KWD",
            "OMR",
            "BHD",
            "NZD",
            "SGD",
            "HKD",
            "MYR",

            "THB",
            "TRY",
            "SEK",
            "NOK",
            "DKK",
            "RUB",
            "ZAR",
            "BRL",
            "MXN",
            "KRW"

    };



    @Override
    public void start(Stage stage) {

        //-----------------------------------
        // Header
        //-----------------------------------

        Label title = new Label(
                "The Financial Translation Engine"
        );

        title.setFont(Font.font(
                "Segoe UI",
                FontWeight.EXTRA_BOLD,
                30
        ));

        title.setTextFill(Color.WHITE);

        Label subtitle = new Label(
                "Enterprise Grade Currency Conversion Pipeline"
        );

        subtitle.setTextFill(Color.web("#CBD5E1"));

        VBox header = new VBox(5,title,subtitle);

        header.setAlignment(Pos.CENTER);



        //-----------------------------------
        // Amount
        //-----------------------------------

        Label amountLabel = new Label("Monetary Amount");

        amountLabel.setTextFill(Color.WHITE);

        amountField = new TextField();

        amountField.setPromptText(
                "Enter Amount"
        );



        //-----------------------------------
        // Source Currency
        //-----------------------------------

        Label sourceLabel =
                new Label("Source Currency");

        sourceLabel.setTextFill(Color.WHITE);

        sourceCurrency = new ComboBox<>();

        sourceCurrency.getItems().addAll(currencies);

        sourceCurrency.setValue("USD");



        //-----------------------------------
        // Target Currency
        //-----------------------------------

        Label targetLabel =
                new Label("Target Currency");

        targetLabel.setTextFill(Color.WHITE);

        targetCurrency = new ComboBox<>();

        targetCurrency.getItems().addAll(currencies);

        targetCurrency.setValue("PKR");



        //-----------------------------------
        // Buttons
        //-----------------------------------

        convertButton =
                new Button("Convert");

        resetButton =
                new Button("Reset");

        swapButton =
                new Button("Swap");

        copyButton =
                new Button("Copy Result");

        styleButton(convertButton,"#2563EB");
        styleButton(resetButton,"#DC2626");
        styleButton(swapButton,"#16A34A");
        styleButton(copyButton,"#9333EA");



        HBox buttonBar =
                new HBox(
                        15,
                        convertButton,
                        swapButton,
                        resetButton,
                        copyButton
                );

        buttonBar.setAlignment(Pos.CENTER);



        //-----------------------------------
        // Result Card
        //-----------------------------------

        resultLabel =
                new Label("Converted Amount : ---");

        resultLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        22
                )
        );

        resultLabel.setTextFill(
                Color.web("#38BDF8")
        );



        rateLabel =
                new Label("Exchange Rate : ---");

        rateLabel.setTextFill(Color.WHITE);



        timeLabel =
                new Label("Last Update : ---");

        timeLabel.setTextFill(Color.LIGHTGRAY);



        statusLabel =
                new Label("System Ready");

        statusLabel.setTextFill(Color.LIGHTGREEN);



        loader =
                new ProgressIndicator();

        loader.setVisible(false);

        loader.setPrefSize(45,45);



        VBox resultCard =
                new VBox(
                        10,
                        resultLabel,
                        rateLabel,
                        timeLabel,
                        statusLabel,
                        loader
                );

        resultCard.setPadding(
                new Insets(20)
        );

        resultCard.setAlignment(Pos.CENTER);

        resultCard.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.web("#1E293B"),
                                new CornerRadii(18),
                                Insets.EMPTY
                        )
                )
        );



        //-----------------------------------
        // History Table
        //-----------------------------------

        historyTable =
                new TableView<>();

        TableColumn<HistoryRecord,String> fromCol =
                new TableColumn<>("From");

        fromCol.setCellValueFactory(
                new PropertyValueFactory<>("from")
        );

        TableColumn<HistoryRecord,String> toCol =
                new TableColumn<>("To");

        toCol.setCellValueFactory(
                new PropertyValueFactory<>("to")
        );

        TableColumn<HistoryRecord,String> amountCol =
                new TableColumn<>("Amount");

        amountCol.setCellValueFactory(
                new PropertyValueFactory<>("amount")
        );

        TableColumn<HistoryRecord,String> resultCol =
                new TableColumn<>("Result");

        resultCol.setCellValueFactory(
                new PropertyValueFactory<>("result")
        );

        historyTable.getColumns().addAll(
                fromCol,
                toCol,
                amountCol,
                resultCol
        );

        historyTable.setItems(history);

        historyTable.setPrefHeight(200);



        //-----------------------------------
        // Form
        //-----------------------------------

        GridPane form =
                new GridPane();

        form.setHgap(20);
        form.setVgap(20);

        form.add(amountLabel,0,0);
        form.add(amountField,1,0);

        form.add(sourceLabel,0,1);
        form.add(sourceCurrency,1,1);

        form.add(targetLabel,0,2);
        form.add(targetCurrency,1,2);



        //-----------------------------------
        // Root Layout
        //-----------------------------------

        VBox root =
                new VBox(
                        25,
                        header,
                        form,
                        buttonBar,
                        resultCard,
                        historyTable
                );

        root.setAlignment(Pos.TOP_CENTER);

        root.setPadding(new Insets(30));

        root.setBackground(
                new Background(
                        new BackgroundFill(
                                new LinearGradient(
                                        0,
                                        0,
                                        1,
                                        1,
                                        true,
                                        CycleMethod.NO_CYCLE,
                                        new Stop(0,Color.web("#0F172A")),
                                        new Stop(1,Color.web("#111827"))
                                ),
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                )
        );

        root.setEffect(new DropShadow());

        Scene scene =
                new Scene(root,900,760);

        stage.setScene(scene);

        stage.setTitle(
                "The Financial Translation Engine"
        );

        stage.show();
        
        convertButton.setOnAction(e -> {

            try {

                securityGate();

                loader.setVisible(true);

                convertCurrency();

            }

            catch (Exception ex) {

                loader.setVisible(false);

                showError(ex.getMessage());

            }

        });


        resetButton.setOnAction(e -> {

            amountField.clear();

            sourceCurrency.setValue("USD");

            targetCurrency.setValue("PKR");

            resultLabel.setText("Converted Amount : ---");

            rateLabel.setText("Exchange Rate : ---");

            timeLabel.setText("Last Update : ---");

            statusLabel.setText("System Reset");

            history.clear();

        });


        swapButton.setOnAction(e -> {

            String temp = sourceCurrency.getValue();

            sourceCurrency.setValue(targetCurrency.getValue());

            targetCurrency.setValue(temp);

        });


        copyButton.setOnAction(e -> {

            javafx.scene.input.Clipboard clipboard =
                    javafx.scene.input.Clipboard.getSystemClipboard();

            javafx.scene.input.ClipboardContent content =
                    new javafx.scene.input.ClipboardContent();

            content.putString(resultLabel.getText());

            clipboard.setContent(content);

            statusLabel.setText("Result Copied");

        });

    }



    private void securityGate() {

        String text = amountField.getText().trim();

        if(text.isEmpty()) {

            throw new IllegalArgumentException(
                    "Enter Amount."
            );

        }

        double value;

        try {

            value = Double.parseDouble(text);

        }

        catch(NumberFormatException ex){

            throw new NumberFormatException(
                    "Only Numbers Allowed."
            );

        }

        if(value < 0){

            amountField.clear();

            amountField.requestFocus();

            throw new IllegalArgumentException(
                    "Negative Amount Rejected."
            );

        }

    }



    private void convertCurrency() {

        try {

            String from = sourceCurrency.getValue();

            String to = targetCurrency.getValue();

            double amount =
                    Double.parseDouble(
                            amountField.getText()
                    );

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpRequest request =
                    HttpRequest.newBuilder()

                            .uri(

                                    URI.create(

                                            "https://open.er-api.com/v6/latest/" + from

                                    )

                            )

                            .GET()

                            .build();

            HttpResponse<String> response =

                    client.send(

                            request,

                            HttpResponse.BodyHandlers.ofString()

                    );

            String json = response.body();

            double rate = getRate(json,to);

            double result = amount * rate;

            loader.setVisible(false);

            resultLabel.setText(

                    "Converted Amount : "

                    + df.format(result)

                    + " "

                    + to

            );

            rateLabel.setText(

                    "1 "

                    + from

                    + " = "

                    + df.format(rate)

                    + " "

                    + to

            );

            timeLabel.setText(

                    "Updated : "

                    + LocalDateTime.now()

                            .toLocalTime()

                            .withNano(0)

            );

            statusLabel.setText(

                    "Conversion Successful"

            );

            history.add(

                    new HistoryRecord(

                            from,

                            to,

                            df.format(amount),

                            df.format(result)

                    )

            );

        }

        catch(Exception ex){

            loader.setVisible(false);

            showError(

                    "API Connection Failed."

            );

        }

    }
        private double getRate(String json, String currency) {

        String key = "\"" + currency + "\":";

        int start = json.indexOf(key);

        if (start == -1) {

            throw new RuntimeException("Currency Not Found");

        }

        start += key.length();

        int end = start;

        while (end < json.length()) {

            char c = json.charAt(end);

            if (!(Character.isDigit(c) || c == '.')) {

                break;

            }

            end++;

        }

        return Double.parseDouble(

                json.substring(start, end)

        );

    }



    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Financial Translation Engine");

        alert.setHeaderText("Security Gate");

        alert.setContentText(message);

        alert.showAndWait();

        Platform.runLater(() -> amountField.requestFocus());

    }



    private void styleButton(Button button, String color) {

        button.setStyle(

                "-fx-background-color:" + color + ";" +

                "-fx-text-fill:white;" +

                "-fx-font-size:14;" +

                "-fx-font-weight:bold;" +

                "-fx-background-radius:12;" +

                "-fx-padding:12 20;"

        );

        button.setOnMouseEntered(e -> {

            button.setScaleX(1.05);

            button.setScaleY(1.05);

        });

        button.setOnMouseExited(e -> {

            button.setScaleX(1);

            button.setScaleY(1);

        });

    }



    public static class HistoryRecord {

        private final SimpleStringProperty from;

        private final SimpleStringProperty to;

        private final SimpleStringProperty amount;

        private final SimpleStringProperty result;



        public HistoryRecord(

                String from,

                String to,

                String amount,

                String result

        ) {

            this.from = new SimpleStringProperty(from);

            this.to = new SimpleStringProperty(to);

            this.amount = new SimpleStringProperty(amount);

            this.result = new SimpleStringProperty(result);

        }



        public String getFrom() {

            return from.get();

        }



        public String getTo() {

            return to.get();

        }



        public String getAmount() {

            return amount.get();

        }



        public String getResult() {

            return result.get();

        }

    }



    public static void main(String[] args) {

        launch(args);

    }

}