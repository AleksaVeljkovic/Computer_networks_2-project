package Varijanta5;

import com.ireasoning.protocol.snmp.*;
import javax.swing.*;
import javax.swing.table.*;

import java.io.IOException;
import java.text.ParseException;

import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class Varijanta5 extends JFrame{
	
	private SnmpSession sesija = null;
	private SnmpTableModel bgpTabela = null;
	
	private JPanel sadrzajTabele = new JPanel(new GridLayout(1,1));
	private JPanel trenutniPanel = null;
	
	
	private Button dugme = new Button("Ispis Tabele");
	private String odabraniRuter = null;
	private String ipAdress = null;
	
	
	
	public Varijanta5() {
		
		try {
			SnmpSession.loadMib("BGP4-MIB");
		}
		catch(Exception e) {	
			e.printStackTrace();
		}

		
		this.setBounds(700, 200, 1100, 600);
		setTitle("BGP tabela");
		
		
		popuni();										
		
		
		addWindowListener(new WindowAdapter(){	
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
		setVisible(true);				
		
	}
	
private void popuni() {
		
		JPanel sadrzajOpcija = new JPanel(new GridLayout(1,1));	
		
		JPanel ruteri = new JPanel(new GridLayout(1,1));
		ruteri.setBackground(Color.WHITE);						
		ruteri.add(new Label ("Ruter: ")); 						
		
		CheckboxGroup grupaRutera = new CheckboxGroup();		
		
		Checkbox r1 = new Checkbox("R1", true, grupaRutera);
		Checkbox r2 = new Checkbox("R2", false, grupaRutera);
		Checkbox r3 = new Checkbox("R3", false, grupaRutera);
		
		ruteri.add(r1); 
		ruteri.add(r2);			
		ruteri.add(r3);
		
		sadrzajOpcija.add(ruteri);		
		
		
		dugme.setEnabled(true);		
		JPanel ispis = new JPanel(new GridLayout(1,1));
		ispis.add(dugme);
		sadrzajOpcija.add(ispis);
		
		
		
		
		dugme.addActionListener(new ActionListener() {	

			@Override
			public void actionPerformed(ActionEvent e) {
				odabraniRuter = grupaRutera.getSelectedCheckbox().getLabel(); 
				dohvatiTabelu();		
			}
		
		});
				
		
		add(sadrzajOpcija, BorderLayout.NORTH); 	
		add(sadrzajTabele, BorderLayout.CENTER);
	}

	
	
	private void dohvatiTabelu() {		
		
		try {
			if(odabraniRuter == "R1") { 
				ipAdress = "192.168.10.1";
			}
			if(odabraniRuter == "R2") { 
				ipAdress = "192.168.20.1";
			}
			if(odabraniRuter == "R3") { 
				ipAdress = "192.168.30.1";
			}
			
			
			
			Timer t = new Timer();
			
			t.schedule(new TimerTask() {
				
				@Override
				public void run() {
					
					try {
						sesija = new SnmpSession(ipAdress, 161, "si2019", "si2019", SnmpConst.SNMPV2);		
						bgpTabela = sesija.snmpGetTable("bgp4PathAttrTable");

					}
					catch(Exception e) {}
					
					
					if(trenutniPanel != null) {
						sadrzajTabele.remove(trenutniPanel);
					}
					
					JTable redoviNajboljihRuta = new JTable(bgpTabela);
					
					trenutniPanel = new JPanel(new GridLayout(1,1));
					JTable novaTabela = new JTable(bgpTabela) {
						public Component prepareRenderer(TableCellRenderer renderer, int red, int kolona) {
							Component komponenta = super.prepareRenderer(renderer, red, kolona);
							
				            if(!komponenta.getBackground().equals(getSelectionBackground())) {
				               String str = (String)redoviNajboljihRuta.getValueAt(red, 12);
				               if(str.equals("2")) {
				            	   komponenta.setForeground(Color.green);
				               }
				               else {
				            	   komponenta.setForeground(Color.red);
				               }
				               komponenta.setBackground(Color.gray);
				            }
							return komponenta;
						}
					};
					
					JScrollPane tabelaSaZaglavljem = new JScrollPane(novaTabela);
					
					try {
						
						novaTabela.removeColumn(novaTabela.getColumn("bgp4PathAttrPeer"));
						novaTabela.removeColumn(novaTabela.getColumn("bgp4PathAttrCalcLocalPref"));
						novaTabela.removeColumn(novaTabela.getColumn("bgp4PathAttrUnknown"));
						novaTabela.removeColumn(novaTabela.getColumn("bgp4PathAttrIpAddrPrefixLen"));
						
						novaTabela.getColumn("bgp4PathAttrIpAddrPrefix").setHeaderValue("Addr Pref");
						novaTabela.getColumn("bgp4PathAttrOrigin").setHeaderValue("Origin");
						novaTabela.getColumn("bgp4PathAttrASPathSegment").setHeaderValue("AS Path");
						novaTabela.getColumn("bgp4PathAttrNextHop").setHeaderValue("Next Hop");
						novaTabela.getColumn("bgp4PathAttrMultiExitDisc").setHeaderValue("MED");
						novaTabela.getColumn("bgp4PathAttrLocalPref").setHeaderValue("Local Pref");
						novaTabela.getColumn("bgp4PathAttrAggregatorAS").setHeaderValue("Aggregator AS");
						novaTabela.getColumn("bgp4PathAttrAggregatorAddr").setHeaderValue("Aggregator Address");
						novaTabela.getColumn("bgp4PathAttrAtomicAggregate").setHeaderValue("Atomic Aggregate");
						novaTabela.getColumn("bgp4PathAttrBest").setHeaderValue("Best");
						

					}
					catch(Exception e) {}
					
		
					trenutniPanel.add(tabelaSaZaglavljem);
					sadrzajTabele.add(trenutniPanel);
					validate();
				}
			
			}, 0, 10000);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) { 
		new Varijanta5();
	}

}