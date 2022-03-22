package Helper.Log;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.EventQueue;	
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import javax.swing.JButton;	
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
	
/**
 * window that is shown while the method process works
 * @author abischoff
 *
 */
public class LogFrame extends JFrame {
	
	private static final long serialVersionUID = 6954718621937740930L;
	private JTextArea textArea;

		public LogFrame() {
			setTitle("Progress");
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			textArea = new JTextArea("");
			DefaultCaret caret = (DefaultCaret)textArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			textArea.setEditable(false);
			scrollPane.setViewportView(textArea);

			JButton btnNewButton = new JButton("Cancel");
			btnNewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int i = JOptionPane.showConfirmDialog(null,  "Should the process be canceled?", 
							"Cancel process", JOptionPane.OK_CANCEL_OPTION);
					if (i == JOptionPane.OK_OPTION) {
						setVisible(false);
						textArea = new JTextArea("");
						Logging.closeProcessWindow();
					}
				}
			});
			getContentPane().add(btnNewButton, BorderLayout.SOUTH);
			setSize(300,150);
			setLocationRelativeTo(null);	
		}

		/**
		 * prints an text to the log frame
		 * @param text
		 */
		public void printProgress(String text) {
			textArea.append(text+"\n");
		}

		/**
		 * makes this frame visible
		 */
		public void makeVisible() {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
					textArea.setText("");
					setVisible(true);
				}
			});
		}
}
