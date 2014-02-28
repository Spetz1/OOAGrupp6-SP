/**
 * Dialog for fetching a user in the admin GUI from the server. Receives a list from the server with all the users that it can fetch.
 * 
 * @author David Stromner
 * @version 2014-02-25
 */

package view.dialog;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Communication;
import model.User;
import controller.ActionHandler;

public class GetUserDialog extends CustomDialog implements Observer {
	private static final long serialVersionUID = 5483970718795036901L;

	public GetUserDialog(Communication communication) {
		super(communication);

		communication.addObserver(this);
		communication.send("GetAllUsers");
		setTitle("Get User");
	}

	public void update(Observable o, Object arg) {
		if (o instanceof Communication) {
			LinkedList<Object> argsList = (LinkedList<Object>) arg;

			// Check what type of message was received
			if (((String) argsList.get(0)).compareToIgnoreCase("GetAllUsers") == 0) {
				argsList.removeFirst();
				// TODO STOP LOOPING!
				for (Object s : argsList) {
					((JComboBox) components.get("gUComboBox")).addItem(s);
				}
			} else if (((String) argsList.get(0))
					.compareToIgnoreCase("GetUser") == 0) {
				User user = (User) argsList.get(1);
			}
		}
	}

	/**
	 * Create all the components that is going to be used inside the dialog.
	 */
	@Override
	protected void create() {
		super.create();
		Container temp;
		
		// Panel
		temp = new JPanel();
		((JPanel) temp).setLayout(new GridBagLayout());
		components.put("gUPanel", temp);

		// FilterLabel
		temp = new JLabel("Filter: ");
		components.put("gUFilterLabel", temp);

		// TextField
		temp = new JTextField();
		components.put("gUText", temp);

		// ComboBox
		temp = new JComboBox<String>();
		components.put("gUComboBox", temp);

		// An OKButton needs to be created in a subclass to CustomDialog since
		// each OKButton is going to trigger different things.
		// OkButton
		temp = new JButton("Ok");
		((JButton) temp).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionHandler.getInstance().getUserDialogOk(customDialog, components.get("gUComboBox"));
			}
		});
		components.put("okButton", temp);
	}
	
	/**
	 * Place all the created components.
	 */
	@Override
	protected void build() {
		super.build();
		
		GridBagConstraints c;
		JPanel canvas = getCanvas();
		JPanel getUser = (JPanel) components.get("gUPanel");
		getUser.setSize(getUser.getPreferredSize());

		// Panel
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		canvas.add(getUser, c);

		// FilterLabel
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		getUser.add(components.get("gUFilterLabel"), c);

		// TextField
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.ipadx = 65;
		c.insets = new Insets(0, 0, 0, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		getUser.add(components.get("gUText"), c);

		// ComboBox
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		c.ipadx = 65;
		c.fill = GridBagConstraints.HORIZONTAL;
		getUser.add(components.get("gUComboBox"), c);
	}
}
