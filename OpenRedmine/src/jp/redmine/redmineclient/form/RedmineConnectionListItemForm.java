package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class RedmineConnectionListItemForm extends FormHelper {
	public TextView textSubject;
	public RedmineConnectionListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(android.R.id.text1);
	}


	public void setValue(RedmineConnection rd){
		textSubject.setText(rd.getName());

	}

}

