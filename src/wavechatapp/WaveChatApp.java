package wavechatapp;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import javax.swing.border.SoftBevelBorder;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

import javax.swing.border.EtchedBorder;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class WaveChatApp extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField txtUsername;
	private JTextField txtGrpName;
	private JPanel panelStatus;
	private JLabel lblStatus;
	private JList UserList;
	
	MulticastSocket multicastSocket= null, commonMulticastSocket = null;
	InetAddress multicastGroup = null, commonMulticastGroup = null;
	
	private String myUsername="", processID;
	private boolean usernameAvailability;
	private boolean userStatus = false;
	List<String> userArray = new ArrayList<String>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WaveChatApp frame = new WaveChatApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WaveChatApp() {
		
		processID = ManagementFactory.getRuntimeMXBean().getName();
		
		try{
			commonMulticastGroup = InetAddress.getByName("230.1.1.1");
			commonMulticastSocket = new MulticastSocket(6789);
			commonMulticastSocket.joinGroup(commonMulticastGroup);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte buf1[] = new byte[1000];
				DatagramPacket dgpReceived = new DatagramPacket(buf1, buf1.length);
				while (true){
					try{
						commonMulticastSocket.receive(dgpReceived);
						byte[] receivedData = dgpReceived.getData();
						int length = dgpReceived.getLength();
						String command = new String(receivedData,0,length);
						String[] InD = command.split(":");
						commonCommandHandler(InD);
					}catch(IOException ex){
						ex.printStackTrace();
					}
				}
			}
			
		}).start();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1037, 648);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("GROUP 7 WAVECHAT APP");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 23));
		lblNewLabel.setBounds(12, 0, 343, 60);
		contentPane.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(12, 55, 375, 533);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Username :");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_2.setBounds(12, 46, 73, 16);
		panel.add(lblNewLabel_2);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(90, 43, 169, 22);
		panel.add(txtUsername);
		txtUsername.setColumns(10);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (txtUsername.getText().equals("")){
					JOptionPane.showMessageDialog(new JFrame(), "Username not specified", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else if (txtUsername.getText().contains(" ")){
					JOptionPane.showMessageDialog(new JFrame(), "Username must NOT have any whitespaces", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else if (txtUsername.getText().substring(0, 1).matches("[0-9]")){
					JOptionPane.showMessageDialog(new JFrame(), "Username must NOT begin with a numeric", "Error", JOptionPane.ERROR_MESSAGE);
				}else{
					try{
						usernameAvailability = true;
						String commandOnCheck = "CheckUser:"+txtUsername.getText().trim()+":"+processID;
						byte[] buf = commandOnCheck.getBytes();
						DatagramPacket dgpUserCheck = new DatagramPacket(buf,buf.length, commonMulticastGroup, 6789);
						commonMulticastSocket.send(dgpUserCheck);
						
						TimeUnit.MILLISECONDS.sleep(5);
						
						if (usernameAvailability == true){
							btnRegister.setEnabled(false);
							myUsername = txtUsername.getText().toString();
							panelStatus.setBackground(new Color(102,204,0));
							lblStatus.setText("ONLINE");
							String commandOnAddUser = "GETALLUSER:"+myUsername;
							byte[] buf1 = commandOnAddUser.getBytes(); 
							DatagramPacket dgpNewUser = new DatagramPacket(buf1,buf1.length, commonMulticastGroup, 6789);
							commonMulticastSocket.send(dgpNewUser);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
						
			}
		});
		btnRegister.setBounds(269, 42, 97, 25);
		panel.add(btnRegister);
		
		JLabel label = new JLabel("");
		label.setFont(new Font("Tahoma", Font.BOLD, 13));
		label.setBounds(12, 13, 73, 16);
		panel.add(label);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 75, 354, 10);
		panel.add(separator);
		
		JLabel lblNewLabel_4 = new JLabel("Account Management");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_4.setBounds(12, 13, 209, 20);
		panel.add(lblNewLabel_4);
		
		JLabel lblFriends = new JLabel("Online Users");
		lblFriends.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFriends.setBounds(12, 86, 209, 20);
		panel.add(lblFriends);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 119, 351, 153);
		panel.add(scrollPane_1);
		
		UserList = new JList();
		scrollPane_1.setViewportView(UserList);
		UserList.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 285, 351, 2);
		panel.add(separator_1);
		
		JLabel lblGroups = new JLabel("Groups");
		lblGroups.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblGroups.setBounds(12, 300, 209, 20);
		panel.add(lblGroups);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(12, 367, 351, 153);
		panel.add(scrollPane_2);
		
		JList listGroups = new JList();
		listGroups.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		scrollPane_2.setViewportView(listGroups);
		
		JLabel lblGroupName = new JLabel("Group Name: ");
		lblGroupName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblGroupName.setBounds(12, 333, 84, 16);
		panel.add(lblGroupName);
		
		txtGrpName = new JTextField();
		txtGrpName.setBounds(90, 330, 169, 22);
		panel.add(txtGrpName);
		txtGrpName.setColumns(10);
		
		JLabel label_1 = new JLabel("Username :");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_1.setBounds(276, 46, 73, 16);
		panel.add(label_1);
		
		JButton btnCreateGrp = new JButton("Create");
		btnCreateGrp.setEnabled(false);
		btnCreateGrp.setBounds(269, 329, 97, 25);
		panel.add(btnCreateGrp);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(389, 55, 618, 533);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblAC = new JLabel("Active Conversation: ");
		lblAC.setBounds(12, 13, 158, 27);
		lblAC.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_1.add(lblAC);
		
		JLabel lblNewLabel_1 = new JLabel("Message : ");
		lblNewLabel_1.setBounds(12, 499, 70, 16);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel_1.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(89, 496, 409, 22);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setBounds(509, 495, 97, 25);
		panel_1.add(btnSend);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(12, 41, 594, 439);
		panel_1.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		textArea.setEnabled(false);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		
		JLabel lblActiveChat = new JLabel("-");
		lblActiveChat.setBounds(206, 19, 318, 16);
		panel_1.add(lblActiveChat);
		
		panelStatus = new JPanel();
		panelStatus.setBounds(888, 13, 119, 36);
		contentPane.add(panelStatus);
		panelStatus.setBackground(new Color(204, 0, 0));
		panelStatus.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		panelStatus.setLayout(null);
		
		lblStatus = new JLabel("OFFLINE");
		lblStatus.setBounds(36, 0, 48, 34);
		panelStatus.add(lblStatus);
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 13));
	}
	
	private void commonCommandHandler(String[] inD) {
		// TODO Auto-generated method stub
		try{
			if (inD[0].equals("CheckUser")){
				if (!(myUsername.equals(""))){
					if (inD[1].equals(myUsername.trim()) && !(processID.equals(inD[2]))){
						String response = "UsernameResponse:True:"+inD[2].toString();
						byte[] buf = response.getBytes();
						DatagramPacket dgpCheckUser = new DatagramPacket(buf,buf.length,commonMulticastGroup,6789);
						commonMulticastSocket.send(dgpCheckUser);
					}
				}
			}else if (inD[0].equals("UsernameResponse")){
				if (inD[1].equals("True")&& inD[2].equals(processID)){
					usernameAvailability = false;
					JOptionPane.showMessageDialog(new JFrame(), "Username in used.","Error", JOptionPane.ERROR_MESSAGE);
				}
			}else if (inD[0].equals("GETALLUSER")){
				userArray = new ArrayList<String>(); 
				String name = "NewUserAll:"+myUsername;
				byte[] buf = name.getBytes();
				DatagramPacket dgpAllNames = new DatagramPacket(buf,buf.length,commonMulticastGroup,6789);
				commonMulticastSocket.send(dgpAllNames);
			}else if (inD[0].equals("NewUserAll")){
				userArray.add(inD[1].toString());
				refreshOUser();
			}
			
//			}else if (inD[0].equals("NewUser")){
//				userArray.add(inD[1].toString());
//				String response = "NewUserWelcome:"+myUsername+":"+inD[2];
//				byte[] buf = response.getBytes();
//				DatagramPacket dgpWelcome = new DatagramPacket(buf,buf.length,commonMulticastGroup,6789);
//				commonMulticastSocket.send(dgpWelcome);
//				refreshOUser();
//			}else if (inD[0].equals("NewUserWelcome")){
//				if (!(processID.equals(inD[2]))){
//					String name = "NewUserAll:"+myUsername+":"+inD[2];
//					byte[] buf = name.getBytes();
//					DatagramPacket dgpAllNames = new DatagramPacket(buf,buf.length,commonMulticastGroup,6789);
//					commonMulticastSocket.send(dgpAllNames);
//				}
//			}else if (inD[0].equals("NewUserAll")){
//				if (processID.equals(inD[2])){
//					userArray.add(inD[1].toString());
//					refreshOUser();
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void refreshOUser(){
		String[] uArray  = new String[userArray.size()];
		userArray.toArray(uArray);
		UserList.setListData(uArray);
	}
}
