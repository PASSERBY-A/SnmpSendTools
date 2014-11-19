import jpcap.*;
import jpcap.packet.*;

import java.awt.Rectangle;
import java.lang.String;
import javax.swing.*;

public class CapturePK extends JFrame implements PacketReceiver {
	private JPanel jContentPane = null;
	private JButton start = null;
	private JButton Stop = null;
	private JButton End = null;
	private JButton Analy = null;
	private JTextArea jta = null;
	private JScrollPane jsp = null;
	private JScrollPane udpcon = null;
	private JTextArea udpcontext = null;
	public static Boolean begin = false;
	public byte[] data = null;

	public CapturePK() {
		super();
		initialize();

	}

	private void initialize() {
		this.setSize(600, 500);
		this.setContentPane(getJContentPane());
		this.setTitle("Send");
		begin = false;
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getStart(), null);
			jContentPane.add(getEnd(), null);
			jContentPane.add(getStop(), null);
			jContentPane.add(getAnaly(), null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(getJScrollPane1(), null);
		}
		return jContentPane;
	}

	private JScrollPane getJScrollPane() {
		if (jsp == null) {
			jsp = new JScrollPane();
			jsp.setBounds(new Rectangle(20, 80, 500, 200));
			jsp.setViewportView(getJta());
		}
		return jsp;
	}

	private JScrollPane getJScrollPane1() {
		if (udpcon == null) {
			udpcon = new JScrollPane();
			udpcon.setBounds(new Rectangle(20, 300, 500, 150));
			udpcon.setViewportView(getJUDP());
		}
		return udpcon;
	}

	private JTextArea getJUDP() {
		if (udpcontext == null) {
			udpcontext = new JTextArea();
		}
		udpcontext.append("源Port \t" + "目的Port\t" + "Version\t"
				+ "Community\t\t" + "Command\t" + "ResquestId\t"
				+ "Error_Status\t" + "Error_index\t" + "Oid\t\t"
				+ "OidValue\r\n");
		return udpcontext;
	}

	private JTextArea getJta() {
		if (jta == null) {
			jta = new JTextArea();
		}
		jta.append("源IP\t\t" + "目的IP\t\t" + "Protocol\t" + "Version\n");
		return jta;
	}

	private JButton getStart() {
		if (start == null) {
			start = new JButton();
			start.setBounds(new Rectangle(10, 30, 80, 20));
			start.setText("Start");
			start.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Boolean b = true;
					setBeginValue(b);
					capture();
				}
			});
		}
		return start;
	}

	private JButton getEnd() {
		if (End == null) {
			End = new JButton();
			End.setBounds(new Rectangle(200, 30, 80, 20));
			End.setText("End");
			End.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(1);
				}
			});
		}
		return End;
	}

	private JButton getStop() {
		if (Stop == null) {
			Stop = new JButton();
			Stop.setBounds(new Rectangle(100, 30, 80, 20));
			Stop.setText("Stop");
			Stop.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Boolean b = false;
					setBeginValue(b);
				}
			});
		}
		return Stop;
	}

	public JButton getAnaly() {
		if (Analy == null) {
			Analy = new JButton();
			Analy.setBounds(new Rectangle(300, 30, 80, 20));
			Analy.setText("Clear");
			Analy.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jta.setText("");
					jta.append("源IP\t\t" + "目的IP\t\t" + "Protocol\t"
							+ "Version\n");
					udpcontext.setText("");
					udpcontext.append("源Port \t" + "目的Port\t" + "Version\t"
							+ "Community\t\t" + "Command\t" + "ResquestId\t"
							+ "Error_Status\t" + "Error_index\t" + "Oid\t\t"
							+ "OidValue\r\n");
				}
			});
		}
		return Analy;
	}

	private void Analy(UDPPacket pk) {
		data = pk.data;
		String data1;
		String temp;
		int bits;
		int length;
		int len;
		data1 = byteToString(data);
		String version;
		String Community;
		String Command;
		String ResquestId;
		String Error_index;
		String Error_status;
		String Oid = null;
		String Oidvalue;
		// 获取版本号
		System.out.println(data1);
		temp = data1.substring(2, 4);
		bits = Judgebit(temp) * 2 + 2;
		version = data1.substring(bits, bits + 6);
		// 获取Community
		bits = bits + 6;
		Community = data1.substring(bits, bits + 16);
		// get Command
		bits = bits + 16;
		Command = data1.substring(bits, bits + 2);
		// 获取Command后面的数据(include itself)
		data1 = data1.substring(bits);
		temp = data1.substring(2, 4);// get the value behind command
		// 获取requestid
		bits = Judgebit(temp) * 2 + 2;
		int end = data1.indexOf("020100");
		ResquestId = data1.substring(bits, end);
		// 获取Error_status
		Error_status = data1.substring(end, end + 6);
		// 获取Error_index
		Error_index = data1.substring(end + 6, end + 12);

		udpcontext.append(pk.src_port + "\t" + pk.dst_port + "\t" + version
				+ "\t" + Community + "\t" + Command + "\t" + ResquestId + "\t"
				+ Error_status + "\t" + Error_index + "\t");
		// data1只保留oid oidvalue 数据部分内容
		data1 = data1.substring(end + 12);
		// get the 结构类型后面的值
		temp = data1.substring(2, 4);
		bits = Judgebit(temp);
		// 获取从第一个oid开始的数据
		data1 = data1.substring(2 + bits * 2);
		// get the length of oid and oidvalue
		String temp1 = data1.substring(2, 4);
		// show the number of oid
		// length=Judge(temp);
		length = data1.length();
		len = (Judge(temp1) + 2) * 2;
		int number = length / len;
		// get the oid
		for (int i = 0; i < number; i++) {
			temp = data1.substring(6, 8);
			length = Judge(temp);
			temp1 = data1.substring(2, 4);
			len = Judge(temp1);
			Oid = data1.substring(8, 8 + length * 2);
			bits = 8 + length * 2;
			int ends = (len + 2) * 2;
			Oidvalue = data1.substring(bits, ends);
			data1 = data1.substring(ends);
			udpcontext.append(Oid + "\t" + Oidvalue + "\t");
		}
		udpcontext.append("\r\n");
	}

	private int Judgebit(String s) {
		int bits = 0;
		if (s.compareTo("80") < 0) {
			bits = 1;// 长度只有两个字节
		}
		if (s.equals("81")) {
			bits = 2;
		}
		if (s.equals("82")) {
			bits = 3;
		}
		return bits;
	}

	private int Judge(String s) {
		int temp = 0;
		int high = 0;
		int low = 0;
		if (s.compareToIgnoreCase("0A") < 0) {
			temp = Integer.parseInt(s);
		} else {
			String h = s.substring(0, 1);
			String l = s.substring(1, 2);
			high = change(h);
			low = change(l);
			temp = high * 16 + low;
		}
		return temp;
	}

	private int change(String s)// 转化为十进制数
	{
		int temp = 0;
		if (s.compareToIgnoreCase("A") < 0) {
			temp = Integer.parseInt(s);
		} else {
			if (s.equalsIgnoreCase("A"))
				temp = 10;
			if (s.equalsIgnoreCase("B"))
				temp = 11;
			if (s.equalsIgnoreCase("C"))
				temp = 12;
			if (s.equalsIgnoreCase("D"))
				temp = 13;
			if (s.equalsIgnoreCase("E"))
				temp = 14;
			if (s.equalsIgnoreCase("F"))
				temp = 15;
		}

		return temp;
	}

	private String byteToString(byte[] b) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			byte high, low;
			byte maskHigh = (byte) 0xf0;
			byte maskLow = 0x0f;
			high = (byte) ((b[i] & maskHigh) >> 4);
			low = (byte) (b[i] & maskLow);
			buf.append(findHex(high));
			buf.append(findHex(low));
		}
		return buf.toString();
	}

	private static char findHex(byte b) {
		int t = new Byte(b).intValue();
		t = t < 0 ? t + 16 : t;

		if ((0 <= t) && (t <= 9)) {
			return (char) (t + '0');
		}

		return (char) (t - 10 + 'A');
	}

	private Boolean setBeginValue(Boolean b) {
		begin = b;
		return begin;
	}

	private Boolean getBeginValue() {
		return begin;
	}

	public void receivePacket(Packet packet) // 实现接口PacketReceiver类中的receivePacket方法
	{
		System.out.println("ok");
	}

	public void capture() {
		Packet pa;
		try {
			NetworkInterface[] NI = JpcapCaptor.getDeviceList(); // 获取设备列表名
			JpcapCaptor jpcap1 = JpcapCaptor.openDevice(NI[1], 1000, true, 50); // 打开网卡设备
			for (int i = 0; i < 200; i++) {
				begin = getBeginValue();
				if (begin) {
					pa = jpcap1.getPacket(); // 获取数据包
					if (pa instanceof IPPacket) // 判断是否为IP数据包
					{
						IPPacket ip = (IPPacket) pa;
						jta.append(ip.src_ip + "\t\t" + ip.dst_ip + "\t\t"
								+ ip.protocol + "\t" + ip.version);
						jta.append("\r\n");
						if (ip.protocol == 17) {
							UDPPacket udp = (UDPPacket) pa;
							if (udp.src_port == 161 || udp.dst_port == 162) {
								Analy(udp);
							}
						}
					}
				} else
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) { // 定义变量用存放数据包的相应的信息
		CapturePK cpk = new CapturePK();
		cpk.setVisible(true);
	}
}