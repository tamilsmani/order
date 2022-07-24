package com.stock.view;

import java.awt.Color;
import java.awt.DefaultKeyboardFocusManager;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.stock.client.PaperTrade;
import com.stock.client.StockAPI;
import com.stock.model.ComboItem;
import com.stock.model.NFOMasterEnum;
import com.stock.model.StrikeModel;
import com.stock.model.TradeDataEnum;

import lombok.SneakyThrows;


public class ScalpUI extends javax.swing.JFrame {

    StockAPI stockAPI;
    public CustomKeyEventDispatcher customKeyEventDispatcher;
    Float bookedPL = 0f;
    Float openPL = 0f;
    public DefaultListModel<String> logMessageListModel = new DefaultListModel<>();
    DateTimeFormatter DATE__FORMATTER = DateTimeFormatter.ofPattern("MMM-yyyy");
    int strikeRange = 1500;
    String currentExpiry;
    
	List<StrikeModel> niftyPEOptions = new ArrayList<>();
	List<StrikeModel> niftyCEOptions = new ArrayList<>();
	List<StrikeModel> bankNiftyPEOptions = new ArrayList<>();
	List<StrikeModel> bankNiftyCEOptions = new ArrayList<>();
	
	String selectedTradeOption;
	public String selectedBuySymbol;
	public String selectedSellSymbol;
	public String selectedBuySymbolToken;
	public String selectedSellSymbolToken;
	
	public String currentOrderToken;
	
	public boolean isOrderOpened = false;
    String lotSize = null;
	public boolean setToTradeClicked = false;
	
    public ScalpUI() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
            
            setResizable(false);
           // setFocusTraversalKeysEnabled(false);
              setFocusable(true);
           // pack();
            //setLocationRelativeTo(null);
           // setFocusTraversalKeysEnabled(false);
            
           // addKeyListener(customKeyEventDispatcher);
           
            
      
	        tradeTableModel = new DefaultTableModel();
	        tradeTableModel.addColumn(TradeDataEnum.SYMBOL.getDesc());
	        tradeTableModel.addColumn(TradeDataEnum.TRANS.getDesc());
	        tradeTableModel.addColumn(TradeDataEnum.QTY.getDesc());
	        tradeTableModel.addColumn(TradeDataEnum.AVG.getDesc());
	        tradeTableModel.addColumn("-");
	        tradeTableModel.addColumn(TradeDataEnum.SL.getDesc());
	        tradeTableModel.addColumn("+");
	        tradeTableModel.addColumn(TradeDataEnum.LTP.getDesc());
	
	        tradeTableModel.addColumn(TradeDataEnum.PL.getDesc());
	        tradeTableModel.addColumn(TradeDataEnum.EXIT.getDesc());
	        tradeTableModel.addColumn(TradeDataEnum.STATUS.getDesc());
	
	        tradeTableModel.addColumn(TradeDataEnum.ORDERID.getDesc());
	        initComponents();
	        stockAPI = new PaperTrade(this);
	        loadStockMasterData();
	        
	        
		//	tradeTableModel.setMinusButton(new TableButtonRenderer("-", this));
		//	tradeTableModel.setPlusButton(new TableButtonRenderer("+", this));
		//	tradeTableModel.setExitButton(new TableOrderExitButtonRenderer("Exit", this));
	        customKeyEventDispatcher = new CustomKeyEventDispatcher(this,stockAPI);
	        
			tradeTable.getColumnModel().getColumn(TradeDataEnum.MINUS.getColumnIndex()).setCellEditor(
					new TableButtonRenderer("-", stockAPI));
			tradeTable.getColumnModel().getColumn(TradeDataEnum.PLUS.getColumnIndex()).setCellEditor(
					new TableButtonRenderer("+", stockAPI));
			tradeTable.getColumnModel().getColumn(TradeDataEnum.EXIT.getColumnIndex()).setCellEditor(
					new TableOrderExitButtonRenderer("Exit", stockAPI));
		
	        settingsPanel.setVisible(false);
	        indexOptionCombo.setSelectedIndex(0);
	        indexCombo.setSelectedIndex(0);
//	        indexOptionCombo.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
//	        indexOptionCombo.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

	        tradeTable.setFocusable(false);
	        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(customKeyEventDispatcher);
        } catch (Exception ex) {
            Logger.getLogger(ScalpUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SneakyThrows
    private void loadStockMasterData() {
    	String monthYear =  LocalDate.now().format(DATE__FORMATTER).toUpperCase();
    	DateTimeFormatter EXPIRY_DATE_FORMAT = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("dd-MMM-yyyy").toFormatter();
    	
		// Select only current month contract on Future / Index / Option
		List<String> validLines =  Files.lines(Path.of(new File(System.getProperty("nfo-symbol-location")).toURI())).skip(1).filter(line -> {
			String[] split = line.split(",");
			return split[NFOMasterEnum.EXPIRY.getPosition()].contains(monthYear) && 
				   !split[NFOMasterEnum.SYMBOL.getPosition()].contains(NFOMasterEnum.FINNIFTY.name()) &&
				   !split[NFOMasterEnum.SYMBOL.getPosition()].contains(NFOMasterEnum.MIDCPNIFTY.name());
		})
		.collect(Collectors.toList());
		
		// Nifty Future Index
		List<StrikeModel> niftyFutureIndex =  validLines.stream().filter(line -> {
			String split [] = line.split(",");
			return split[NFOMasterEnum.INSTRUMENT.getPosition()].equals(NFOMasterEnum.FUTIDX.getCode()) && 
				   split[NFOMasterEnum.SYMBOL.getPosition()].equals(NFOMasterEnum.NIFTY.getCode()) ;
		}).map((line) ->{
			return buildModel(line);
		}).collect(Collectors.toList());
		
		
		// Bank NIFTY Future INDEX
		List<StrikeModel> bankNiftyFutureIndex = validLines.stream().filter(line -> {
			String split [] = line.split(",");
			return split[NFOMasterEnum.INSTRUMENT.getPosition()].equals(NFOMasterEnum.FUTIDX.getCode()) && 
				   split[NFOMasterEnum.SYMBOL.getPosition()].equals(NFOMasterEnum.BANKNIFTY.getCode()) ;
		}).map((line) ->{
			return buildModel(line);
		}).collect(Collectors.toList());

//		ComboItem reliance = new  ComboItem("RELIANCE-EQ","RELIANCE", "2885","10");
//		
//		indexCombo.addItem(reliance);
		
//		indexCombo.addItem(new ComboItem(niftyFutureIndex.get(0).getTradingSymbol(), niftyFutureIndex.get(0).getSymbol(),
//				niftyFutureIndex.get(0).getToken(), niftyFutureIndex.get(0).getLotSize()));
		
		indexCombo.addItem(new ComboItem(niftyFutureIndex.get(0).getTradingSymbol(), niftyFutureIndex.get(0).getSymbol(),
				niftyFutureIndex.get(0).getToken(), niftyFutureIndex.get(0).getLotSize()));
		indexCombo.addItem(new ComboItem(bankNiftyFutureIndex.get(0).getTradingSymbol(), bankNiftyFutureIndex.get(0).getSymbol(),
				bankNiftyFutureIndex.get(0).getToken(), bankNiftyFutureIndex.get(0).getLotSize()));

		indexOptionCombo.addItem(new ComboItem(niftyFutureIndex.get(0).getTradingSymbol(), niftyFutureIndex.get(0).getSymbol(),
				niftyFutureIndex.get(0).getToken(), niftyFutureIndex.get(0).getLotSize()));
		indexOptionCombo.addItem(new ComboItem(bankNiftyFutureIndex.get(0).getTradingSymbol(), bankNiftyFutureIndex.get(0).getSymbol(),
				bankNiftyFutureIndex.get(0).getToken(), bankNiftyFutureIndex.get(0).getLotSize()));

		// Option Expiry
    	Set<LocalDate> optionExpirySet = validLines.stream().filter(line -> {
			String split [] = line.split(",");
			return split[NFOMasterEnum.INSTRUMENT.getPosition()].equals(NFOMasterEnum.OPTIDX.getCode()) &&
					split[NFOMasterEnum.SYMBOL.getPosition()].equals(NFOMasterEnum.NIFTY.getCode());
		}).map((line) ->{
			return LocalDate.parse(buildModel(line).getExpiry(), EXPIRY_DATE_FORMAT);
		}).collect(Collectors.toSet());
    	 TreeSet<LocalDate> treeSet = new TreeSet<LocalDate>(optionExpirySet);
    	
    	 currentExpiry = treeSet.stream().filter( date -> {
    		 return LocalDate.now().compareTo(date) <= 0;
    	 }).findFirst().get().format(EXPIRY_DATE_FORMAT).toUpperCase();
    	 
		// Nifty PE Option
		niftyPEOptions = validLines.stream().filter(line -> {
			String split [] = line.split(",");
			return split[NFOMasterEnum.INSTRUMENT.getPosition()].equals(NFOMasterEnum.OPTIDX.getCode()) && 
				   split[NFOMasterEnum.EXPIRY.getPosition()].equals(currentExpiry) &&
				   split[NFOMasterEnum.SYMBOL.getPosition()].equals(NFOMasterEnum.NIFTY.getCode()) &&
				   split[NFOMasterEnum.OPTION_TYPE.getPosition()].equals(NFOMasterEnum.PE.getCode());
		}).map((line) ->{
			return buildModel(line);

		}).collect(Collectors.toList());
		
		// Nifty CE Option
		niftyCEOptions = validLines.stream().filter(line -> {
			String split [] = line.split(",");
			return split[NFOMasterEnum.INSTRUMENT.getPosition()].equals(NFOMasterEnum.OPTIDX.getCode()) && 
					   split[NFOMasterEnum.EXPIRY.getPosition()].equals(currentExpiry) &&
					   split[NFOMasterEnum.SYMBOL.getPosition()].equals(NFOMasterEnum.NIFTY.getCode()) &&
					   split[NFOMasterEnum.OPTION_TYPE.getPosition()].equals(NFOMasterEnum.CE.getCode());
		}).map((line) ->{
			return buildModel(line);
		}).collect(Collectors.toList());
		
		// Bank Nifty PE Option
		bankNiftyPEOptions = validLines.stream().filter(line -> {
			String split [] = line.split(",");
			return split[NFOMasterEnum.INSTRUMENT.getPosition()].equals(NFOMasterEnum.OPTIDX.getCode()) && 
				   split[NFOMasterEnum.EXPIRY.getPosition()].equals(currentExpiry) &&
				   split[NFOMasterEnum.SYMBOL.getPosition()].equals(NFOMasterEnum.BANKNIFTY.getCode()) &&
				   split[NFOMasterEnum.OPTION_TYPE.getPosition()].equals(NFOMasterEnum.PE.getCode());
		}).map((line) ->{
			return buildModel(line);

		}).collect(Collectors.toList());
		
		// Bank Nifty CE Option
		bankNiftyCEOptions = validLines.stream().filter(line -> {
			String split [] = line.split(",");
			return split[NFOMasterEnum.INSTRUMENT.getPosition()].equals(NFOMasterEnum.OPTIDX.getCode()) && 
					   split[NFOMasterEnum.EXPIRY.getPosition()].equals(currentExpiry) &&
					   split[NFOMasterEnum.SYMBOL.getPosition()].equals(NFOMasterEnum.BANKNIFTY.getCode()) &&
					   split[NFOMasterEnum.OPTION_TYPE.getPosition()].equals(NFOMasterEnum.CE.getCode());
		}).map((line) ->{
			return buildModel(line);
		}).collect(Collectors.toList());
				
	}
	
	private StrikeModel buildModel(String line) {
		String data[] = line.split(",");
		return StrikeModel.builder()
			.exchange(data[NFOMasterEnum.EXCHANGE.getPosition()])
			.token(data[NFOMasterEnum.TOKEN.getPosition()])
			.lotSize(data[NFOMasterEnum.LOT_SIZE.getPosition()])
			.symbol(data[NFOMasterEnum.SYMBOL.getPosition()])
			.tradingSymbol(data[NFOMasterEnum.TRADING_SYMBOL.getPosition()])
			.expiry(data[NFOMasterEnum.EXPIRY.getPosition()])
			.instrument(data[NFOMasterEnum.INSTRUMENT.getPosition()])
			.optionType(data[NFOMasterEnum.OPTION_TYPE.getPosition()])
			.strikePrice(data[NFOMasterEnum.STRIKE_PRICE.getPosition()])
			.tickSize(Float.parseFloat(data[NFOMasterEnum.TICKET_SIZE.getPosition()]))
		.build();
	}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stockRadioGroup = new javax.swing.ButtonGroup();
        clientInfoPanel = new javax.swing.JPanel();
        clientIdLabel = new javax.swing.JLabel();
        clientIdValue = new javax.swing.JLabel();
        settings = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        stockInfoPanel = new javax.swing.JPanel();
        radioIndexOption = new javax.swing.JRadioButton();
        indexOptionCombo = new javax.swing.JComboBox<>();
        radioIndex = new javax.swing.JRadioButton();
        indexOptionPECombo = new javax.swing.JComboBox<>();
        indexOptionCECombo = new javax.swing.JComboBox<>();
        indexCombo = new javax.swing.JComboBox<>();
        indexOptionPELabel = new javax.swing.JLabel();
        indexOptionCELabel = new javax.swing.JLabel();
        setIndexBtn = new javax.swing.JButton();
        setOptionBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        indexBid = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        indexAsk = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        currentOrderSymbol = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        optionPEBid = new javax.swing.JLabel();
        optionPEAsk = new javax.swing.JLabel();
        optionCEBid = new javax.swing.JLabel();
        optionCEAsk = new javax.swing.JLabel();
        logMessagePanel = new javax.swing.JPanel();
        logMessageScrollPane = new javax.swing.JScrollPane();
        logMessageListCtl = new javax.swing.JList<>(logMessageListModel);
        jButton1 = new javax.swing.JButton();
        settingsPanel = new javax.swing.JPanel();
        lotSizeLabel = new javax.swing.JLabel();
        stopLossLabel = new javax.swing.JLabel();
        lotSizeTxt = new javax.swing.JTextField();
        targetLabel = new javax.swing.JLabel();
        stopLossTxt = new javax.swing.JTextField();
        targetTxt = new javax.swing.JTextField();
        bookPLLabel = new javax.swing.JPanel();
        bookedPLLabel = new javax.swing.JLabel();
        openPLLabel = new javax.swing.JLabel();
        openPositionLabel = new javax.swing.JLabel();
        bookedPLAmount = new javax.swing.JLabel();
        openPLAmount = new javax.swing.JLabel();
        openPositionValue = new javax.swing.JLabel();
        bookedPLLabel1 = new javax.swing.JLabel();
        totalProfit = new javax.swing.JLabel();
        bookedPLLabel2 = new javax.swing.JLabel();
        totalSL = new javax.swing.JLabel();
        closedPositionLabel = new javax.swing.JLabel();
        closedPositionValue = new javax.swing.JLabel();
        tradeInfoPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tradeTable = new javax.swing.JTable(tradeTableModel);

        stockRadioGroup.add(radioIndexOption);
        stockRadioGroup.add(radioIndex);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        clientInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Client Info", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 14))); // NOI18N

        clientIdLabel.setText("Client ID :");

        clientIdValue.setText("123");

        settings.setText("Settings");
        settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsActionPerformed(evt);
            }
        });

        jLabel8.setForeground(new java.awt.Color(255, 0, 51));
        jLabel8.setText("Note: Make sure Trade History table is empty when you open the app");

        javax.swing.GroupLayout clientInfoPanelLayout = new javax.swing.GroupLayout(clientInfoPanel);
        clientInfoPanel.setLayout(clientInfoPanelLayout);
        clientInfoPanelLayout.setHorizontalGroup(
            clientInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientInfoPanelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(clientIdLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(clientIdValue, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(settings)
                .addGap(32, 32, 32)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addContainerGap())
        );
        clientInfoPanelLayout.setVerticalGroup(
            clientInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(clientIdLabel)
                .addComponent(clientIdValue)
                .addComponent(settings)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        stockInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stock Selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 14))); // NOI18N
        stockInfoPanel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        stockRadioGroup.add(radioIndexOption);
        radioIndexOption.setText("Option Index");
        radioIndexOption.setEnabled(false);
        radioIndexOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioIndexOptionActionPerformed(evt);
            }
        });

        indexOptionCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexOptionComboActionPerformed(evt);
            }
        });

        stockRadioGroup.add(radioIndex);
        radioIndex.setText("Index");
        radioIndex.setEnabled(false);
        radioIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioIndexActionPerformed(evt);
            }
        });

        indexOptionPECombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexOptionPEComboActionPerformed(evt);
            }
        });

        indexOptionCECombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexOptionCEComboActionPerformed(evt);
            }
        });

        indexCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexComboActionPerformed(evt);
            }
        });

        indexOptionPELabel.setText("PE");

        indexOptionCELabel.setText("CE");

        setIndexBtn.setBackground(new java.awt.Color(0, 204, 204));
        setIndexBtn.setText("Load");
        setIndexBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setIndexBtnActionPerformed(evt);
            }
        });

        setOptionBtn.setBackground(new java.awt.Color(0, 204, 204));
        setOptionBtn.setText("Load");
        setOptionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOptionBtnActionPerformed(evt);
            }
        });

        jLabel3.setText("Bid");

        indexBid.setBackground(new java.awt.Color(204, 204, 255));
        indexBid.setText("0");
        indexBid.setOpaque(true);

        jLabel4.setText("Ask");

        indexAsk.setBackground(new java.awt.Color(204, 204, 255));
        indexAsk.setText("0");
        indexAsk.setOpaque(true);

        jLabel2.setText("Bid");

        jLabel1.setText("Current Order :");

        currentOrderSymbol.setForeground(new java.awt.Color(255, 0, 51));
        currentOrderSymbol.setText("CO");

        jLabel5.setText("Ask");

        jLabel6.setText("Bid");

        jLabel7.setText("Ask");

        optionPEBid.setBackground(new java.awt.Color(255, 153, 153));
        optionPEBid.setText("0");
        optionPEBid.setOpaque(true);

        optionPEAsk.setBackground(new java.awt.Color(255, 153, 153));
        optionPEAsk.setText("0");
        optionPEAsk.setOpaque(true);

        optionCEBid.setBackground(new java.awt.Color(153, 255, 153));
        optionCEBid.setText("0");
        optionCEBid.setOpaque(true);

        optionCEAsk.setBackground(new java.awt.Color(153, 255, 153));
        optionCEAsk.setText("0");
        optionCEAsk.setOpaque(true);

        javax.swing.GroupLayout stockInfoPanelLayout = new javax.swing.GroupLayout(stockInfoPanel);
        stockInfoPanel.setLayout(stockInfoPanelLayout);
        stockInfoPanelLayout.setHorizontalGroup(
            stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stockInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(stockInfoPanelLayout.createSequentialGroup()
                        .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(radioIndexOption)
                                    .addComponent(radioIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(indexOptionCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(indexCombo, 0, 92, Short.MAX_VALUE))
                                .addGap(62, 62, 62))
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(indexOptionPELabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(indexOptionPECombo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                        .addComponent(optionPEBid, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(35, 35, 35)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(setIndexBtn)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(optionPEAsk, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                        .addComponent(setOptionBtn)
                                        .addGap(35, 35, 35)
                                        .addComponent(indexOptionCELabel))
                                    .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                        .addGap(53, 53, 53)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(optionCEBid, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(12, 12, 12)
                                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(indexOptionCECombo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(optionCEAsk, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
                            .addGroup(stockInfoPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(indexBid, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(indexAsk, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(43, Short.MAX_VALUE))))
                    .addGroup(stockInfoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(currentOrderSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        stockInfoPanelLayout.setVerticalGroup(
            stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stockInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioIndexOption)
                    .addComponent(indexOptionPECombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(indexOptionCECombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(indexOptionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setOptionBtn)
                    .addComponent(indexOptionPELabel)
                    .addComponent(indexOptionCELabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(optionPEBid)
                    .addComponent(optionPEAsk)
                    .addComponent(optionCEBid)
                    .addComponent(optionCEAsk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioIndex)
                    .addComponent(indexCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setIndexBtn)
                    .addComponent(jLabel3)
                    .addComponent(indexBid, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(indexAsk, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(stockInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(currentOrderSymbol))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        logMessagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Log"));

        logMessageScrollPane.setBackground(new java.awt.Color(204, 204, 204));

        logMessageScrollPane.setViewportView(logMessageListCtl);

        jButton1.setBackground(new java.awt.Color(204, 204, 204));
        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout logMessagePanelLayout = new javax.swing.GroupLayout(logMessagePanel);
        logMessagePanel.setLayout(logMessagePanelLayout);
        logMessagePanelLayout.setHorizontalGroup(
            logMessagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logMessagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logMessagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logMessageScrollPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logMessagePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        logMessagePanelLayout.setVerticalGroup(
            logMessagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logMessagePanelLayout.createSequentialGroup()
                .addComponent(jButton1)
                .addGap(2, 2, 2)
                .addComponent(logMessageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 14))); // NOI18N

        lotSizeLabel.setText("Lot Size");

        stopLossLabel.setText("Stop Loss Point");

        lotSizeTxt.setText("1");
        lotSizeTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lotSizeTxtActionPerformed(evt);
            }
        });

        targetLabel.setText("Target Point");

        stopLossTxt.setText("10");
        stopLossTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopLossTxtActionPerformed(evt);
            }
        });

        targetTxt.setText("10");
        targetTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetTxtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(stopLossLabel))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(lotSizeLabel)))
                .addGap(18, 18, 18)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(stopLossTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                    .addComponent(lotSizeTxt))
                .addGap(32, 32, 32)
                .addComponent(targetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(targetTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(359, Short.MAX_VALUE))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lotSizeLabel)
                    .addComponent(lotSizeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stopLossLabel)
                    .addComponent(stopLossTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetLabel)
                    .addComponent(targetTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        bookPLLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "P / L Summary", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 14))); // NOI18N
        bookPLLabel.setFont(bookPLLabel.getFont().deriveFont(bookPLLabel.getFont().getStyle() | java.awt.Font.BOLD, bookPLLabel.getFont().getSize()+2));

        bookedPLLabel.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        bookedPLLabel.setText("Booked P / L :");

        openPLLabel.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        openPLLabel.setText("Open P / L : ");

        openPositionLabel.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        openPositionLabel.setText("Open Position : ");

        bookedPLAmount.setText("0");
        bookedPLAmount.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                bookedPLAmountPropertyChange(evt);
            }
        });

        openPLAmount.setText("0");
        openPLAmount.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                openPLAmountPropertyChange(evt);
            }
        });

        openPositionValue.setText("0");

        bookedPLLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bookedPLLabel1.setText("Total P/ L  :");

        totalProfit.setText("0");
        totalProfit.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                totalProfitPropertyChange(evt);
            }
        });

        bookedPLLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        bookedPLLabel2.setText("Total SL   :");

        totalSL.setText("0");
        totalSL.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                totalSLPropertyChange(evt);
            }
        });

        closedPositionLabel.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        closedPositionLabel.setText("Closed Position : ");

        closedPositionValue.setText("0");

        javax.swing.GroupLayout bookPLLabelLayout = new javax.swing.GroupLayout(bookPLLabel);
        bookPLLabel.setLayout(bookPLLabelLayout);
        bookPLLabelLayout.setHorizontalGroup(
            bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bookPLLabelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bookedPLLabel)
                    .addComponent(bookedPLLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bookPLLabelLayout.createSequentialGroup()
                        .addComponent(totalProfit, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bookedPLLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(totalSL, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bookPLLabelLayout.createSequentialGroup()
                        .addComponent(bookedPLAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(openPLLabel)
                        .addGap(29, 29, 29)
                        .addComponent(openPLAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(openPositionLabel)
                    .addComponent(closedPositionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(openPositionValue, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closedPositionValue, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        bookPLLabelLayout.setVerticalGroup(
            bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bookPLLabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookedPLLabel)
                    .addComponent(openPLLabel)
                    .addComponent(openPositionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(openPLAmount)
                    .addComponent(openPositionValue)
                    .addComponent(bookedPLAmount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bookPLLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookedPLLabel1)
                    .addComponent(bookedPLLabel2)
                    .addComponent(totalSL)
                    .addComponent(totalProfit)
                    .addComponent(closedPositionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(closedPositionValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tradeInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Trade History"));

        //tradeTable.setModel(tradeTableModel);

        TableColumnModel colModel=tradeTable.getColumnModel();
        colModel.removeColumn(colModel.getColumn(colModel.getColumnCount()-1));
        colModel.getColumn(0).setPreferredWidth(150);
        tradeTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tradeTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tradeTable.setMinimumSize(new java.awt.Dimension(50, 250));
        tradeTable.setShowGrid(true);
        jScrollPane1.setViewportView(tradeTable);

        javax.swing.GroupLayout tradeInfoPanelLayout = new javax.swing.GroupLayout(tradeInfoPanel);
        tradeInfoPanel.setLayout(tradeInfoPanelLayout);
        tradeInfoPanelLayout.setHorizontalGroup(
            tradeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tradeInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        tradeInfoPanelLayout.setVerticalGroup(
            tradeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(stockInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(clientInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bookPLLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tradeInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logMessagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clientInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stockInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bookPLLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tradeInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logMessagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resetTradeSelection() {
         lotSize = null;
    	 selectedBuySymbol = null;
         selectedSellSymbol = null;
         setToTradeClicked = false;
         selectedBuySymbolToken = null;
         selectedSellSymbolToken = null;

    }
    private void lotSizeTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lotSizeTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lotSizeTxtActionPerformed

    private void stopLossTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopLossTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stopLossTxtActionPerformed

    private void targetTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_targetTxtActionPerformed

    private void settingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsActionPerformed
       AbstractButton checkbox = (AbstractButton) evt.getSource();
       if(checkbox.getModel().isSelected()) {
           settingsPanel.setVisible(true);
       } else {
           settingsPanel.setVisible(false);
       }
       
 
    }//GEN-LAST:event_settingsActionPerformed
        
    private void bookedPLAmountPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_bookedPLAmountPropertyChange
    	Float value = Float.parseFloat(bookedPLAmount.getText());
        if(value < 0) {
            bookedPLAmount.setForeground(Color.RED);
        } else {
            bookedPLAmount.setForeground(new Color(0,153,153));
        }
    }//GEN-LAST:event_bookedPLAmountPropertyChange

    private void openPLAmountPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_openPLAmountPropertyChange
        Float value = Float.parseFloat(openPLAmount.getText());
        if(value < 0) {
            openPLAmount.setForeground(Color.RED);
        } else {
            openPLAmount.setForeground(new Color(0,153,153));
        }
    }//GEN-LAST:event_openPLAmountPropertyChange

    private void totalProfitPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_totalProfitPropertyChange
        // TODO add your handling code here:
        Float value = Float.parseFloat(totalProfit.getText());
        if(value < 0) {
        	totalProfit.setForeground(Color.RED);
        } else {
        	totalProfit.setForeground(new Color(0,153,153));
        }
    }//GEN-LAST:event_totalProfitPropertyChange

    private void totalSLPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_totalSLPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_totalSLPropertyChange

    private void setOptionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setOptionBtnActionPerformed
        // Load Option PE & CE
    	radioIndexOption.setSelected(true);
        selectedTradeOption = NFOMasterEnum.INDEX_OPTION.getCode();

        if(selectedTradeOption.equalsIgnoreCase(NFOMasterEnum.INDEX_OPTION.getCode())) {
            selectedBuySymbol  = ((ComboItem)indexOptionCECombo.getSelectedItem()).getKey();
            selectedSellSymbol = ((ComboItem)indexOptionPECombo.getSelectedItem()).getKey();
            lotSize = String.valueOf(Integer.parseInt(((ComboItem)indexOptionCECombo.getSelectedItem()).getLotSize()) *
            		Integer.parseInt(lotSizeTxt.getText()));
            
            selectedBuySymbolToken = ((ComboItem)indexOptionCECombo.getSelectedItem()).getToken();
            selectedSellSymbolToken = ((ComboItem)indexOptionPECombo.getSelectedItem()).getToken();
           
        }
        setToTradeClicked = true;
      
    }//GEN-LAST:event_setOptionBtnActionPerformed

    private void setIndexBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setIndexBtnActionPerformed
        // Load Index data
    	radioIndex.setSelected(true);
        selectedTradeOption = NFOMasterEnum.INDEX_FUTURE.getCode();
        if (selectedTradeOption.equalsIgnoreCase(NFOMasterEnum.INDEX_FUTURE.getCode())) {
            selectedBuySymbol  = ((ComboItem)indexCombo.getSelectedItem()).getKey();
            selectedSellSymbol = ((ComboItem)indexCombo.getSelectedItem()).getKey();
            lotSize = String.valueOf(Integer.parseInt(((ComboItem)indexCombo.getSelectedItem()).getLotSize()) *
            		Integer.parseInt(lotSizeTxt.getText()));
            
            selectedBuySymbolToken = ((ComboItem)indexCombo.getSelectedItem()).getToken();
            selectedSellSymbolToken = ((ComboItem)indexCombo.getSelectedItem()).getToken();
        }
        
        setToTradeClicked = true;
    }//GEN-LAST:event_setIndexBtnActionPerformed

    private void indexComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexComboActionPerformed
        // TODO add your handling code here:
        resetTradeSelection();
    }//GEN-LAST:event_indexComboActionPerformed

    private void indexOptionCEComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexOptionCEComboActionPerformed
        // TODO add your handling code here:
        resetTradeSelection();
    }//GEN-LAST:event_indexOptionCEComboActionPerformed

    private void indexOptionPEComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexOptionPEComboActionPerformed
        // TODO add your handling code here:
        resetTradeSelection();
    }//GEN-LAST:event_indexOptionPEComboActionPerformed

    private void radioIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioIndexActionPerformed
        indexOptionCombo.setEnabled(false);
        indexOptionCECombo.setEnabled(false);
        indexOptionPECombo.setEnabled(false);
        setOptionBtn.setEnabled(false);

        indexCombo.setEnabled(true);
        setIndexBtn.setEnabled(true);

        resetTradeSelection();
        selectedTradeOption = NFOMasterEnum.INDEX_FUTURE.getCode();
    }//GEN-LAST:event_radioIndexActionPerformed

    private void indexOptionComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexOptionComboActionPerformed
        JComboBox cmbCombo = (JComboBox) evt.getSource();
        indexOptionPECombo.removeAllItems();
        indexOptionCECombo.removeAllItems();

        int niftyCurrentValue = (16600 / 100 ) * 100;
        int bankNiftyCurrentValue= (36000 / 100 ) * 100;

        if(((ComboItem)cmbCombo.getSelectedItem()).getValue().equalsIgnoreCase(NFOMasterEnum.NIFTY.name())) {
            int niftyLowerRange  = niftyCurrentValue - strikeRange;
            int niftyHigherRange = niftyCurrentValue + strikeRange;

            
            niftyPEOptions.stream().forEach( strikeModel -> {
            	if(Integer.parseInt( strikeModel.getStrikePrice()) >= niftyLowerRange && 
            			Integer.parseInt( strikeModel.getStrikePrice()) <= niftyCurrentValue +200 ) {
            		indexOptionPECombo.addItem(new ComboItem(strikeModel.getTradingSymbol(), strikeModel.getStrikePrice(),
                		strikeModel.getToken(), strikeModel.getLotSize()));
            	}
            });
            niftyCEOptions.stream().forEach( strikeModel -> {
                if(Integer.parseInt( strikeModel.getStrikePrice()) >= niftyCurrentValue-200 && 
            			Integer.parseInt( strikeModel.getStrikePrice()) <= niftyHigherRange ) {
                    indexOptionCECombo.addItem(new ComboItem(strikeModel.getTradingSymbol(), strikeModel.getStrikePrice(),
                    		strikeModel.getToken(), strikeModel.getLotSize()));
                }
            });
        } else if(((ComboItem)cmbCombo.getSelectedItem()).getValue().equalsIgnoreCase(NFOMasterEnum.BANKNIFTY.name())) {

            int bankNiftyLowerRange  = niftyCurrentValue     - strikeRange;
            int bankNiftyHigherRange = bankNiftyCurrentValue + strikeRange;

            bankNiftyPEOptions.stream().forEach( strikeModel -> {
                indexOptionPECombo.addItem(new ComboItem(strikeModel.getTradingSymbol(), strikeModel.getStrikePrice(),
                		strikeModel.getToken(), strikeModel.getLotSize()));
            });
            bankNiftyCEOptions.stream().forEach( strikeModel -> {
                indexOptionCECombo.addItem(new ComboItem(strikeModel.getTradingSymbol(), strikeModel.getStrikePrice(),
                		strikeModel.getToken(), strikeModel.getLotSize()));
            });
        }
        resetTradeSelection();

    }//GEN-LAST:event_indexOptionComboActionPerformed

    private void radioIndexOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioIndexOptionActionPerformed

        indexOptionCombo.setEnabled(true);
        indexOptionCECombo.setEnabled(true);
        indexOptionPECombo.setEnabled(true);
        setOptionBtn.setEnabled(true);

        indexCombo.setEnabled(false);
        setIndexBtn.setEnabled(false);

        resetTradeSelection();
        selectedTradeOption = NFOMasterEnum.INDEX_OPTION.getCode();
    }//GEN-LAST:event_radioIndexOptionActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    	 ((DefaultListModel) logMessageListCtl.getModel()).removeAllElements();;
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    
    public DefaultTableModel tradeTableModel;
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ScalpUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScalpUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScalpUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScalpUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ScalpUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bookPLLabel;
    public javax.swing.JLabel bookedPLAmount;
    private javax.swing.JLabel bookedPLLabel;
    public javax.swing.JLabel bookedPLLabel1;
    private javax.swing.JLabel bookedPLLabel2;
    private javax.swing.JLabel clientIdLabel;
    public javax.swing.JLabel clientIdValue;
    private javax.swing.JPanel clientInfoPanel;
    private javax.swing.JLabel closedPositionLabel;
    public javax.swing.JLabel closedPositionValue;
    public javax.swing.JLabel currentOrderSymbol;
    public javax.swing.JLabel indexAsk;
    public javax.swing.JLabel indexBid;
    public javax.swing.JComboBox<ComboItem> indexCombo;
    public javax.swing.JComboBox<ComboItem> indexOptionCECombo;
    private javax.swing.JLabel indexOptionCELabel;
    public javax.swing.JComboBox<ComboItem> indexOptionCombo;
    public javax.swing.JComboBox<ComboItem> indexOptionPECombo;
    private javax.swing.JLabel indexOptionPELabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JList<String> logMessageListCtl;
    private javax.swing.JPanel logMessagePanel;
    javax.swing.JScrollPane logMessageScrollPane;
    private javax.swing.JLabel lotSizeLabel;
    public javax.swing.JTextField lotSizeTxt;
    public javax.swing.JLabel openPLAmount;
    private javax.swing.JLabel openPLLabel;
    private javax.swing.JLabel openPositionLabel;
    public javax.swing.JLabel openPositionValue;
    public javax.swing.JLabel optionCEAsk;
    public javax.swing.JLabel optionCEBid;
    public javax.swing.JLabel optionPEAsk;
    public javax.swing.JLabel optionPEBid;
    public javax.swing.JRadioButton radioIndex;
    public javax.swing.JRadioButton radioIndexOption;
    public javax.swing.JButton setIndexBtn;
    public javax.swing.JButton setOptionBtn;
    javax.swing.JCheckBox settings;
    private javax.swing.JPanel settingsPanel;
    public javax.swing.JPanel stockInfoPanel;
    private javax.swing.ButtonGroup stockRadioGroup;
    private javax.swing.JLabel stopLossLabel;
    public javax.swing.JTextField stopLossTxt;
    private javax.swing.JLabel targetLabel;
    public javax.swing.JTextField targetTxt;
    public javax.swing.JLabel totalProfit;
    public javax.swing.JLabel totalSL;
    private javax.swing.JPanel tradeInfoPanel;
    public javax.swing.JTable tradeTable;
    // End of variables declaration//GEN-END:variables

}
