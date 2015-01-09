package com.door43.translationstudio.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.door43.translationstudio.R;
import com.door43.translationstudio.events.ChoseProjectEvent;
import com.door43.translationstudio.projects.SudoProject;
import com.door43.translationstudio.projects.Model;
import com.door43.translationstudio.projects.Project;
import com.door43.translationstudio.util.MainContext;

/**
 * This dialog displays a list view that allows the user to dig down through projects and meta projects.
 * When a real project is selected an event containing the project will be fired and the dialog dismissed.
 * If metaId is provided as a bundled argument that meta project's children will be displayed in the list
 * otherwise all listable projects will be shown.
 * setModelList takes presedence over the metaId.
 */
public class ChooseProjectDialog extends DialogFragment {
    private ModelItemAdapter mModelItemAdapter;
    private Model[] mModelList = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.projects);
        View v = inflater.inflate(R.layout.dialog_choose_project, container, false);

        ListView listView = (ListView)v.findViewById(R.id.listView);

        if(mModelList != null) {
            if(mModelItemAdapter == null) mModelItemAdapter = new ModelItemAdapter(MainContext.getContext(), mModelList);
        } else {
            Bundle args = getArguments();
            String id = args.getString("metaId");
            SudoProject p = MainContext.getContext().getSharedProjectManager().getMetaProject(id);
            if(p != null) {
                if (mModelItemAdapter == null) mModelItemAdapter = new ModelItemAdapter(MainContext.getContext(), p.getChildren());
            }
        }

        if(mModelItemAdapter != null) {
            // connect adapter
            listView.setAdapter(mModelItemAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Model m = mModelItemAdapter.getItem(i);
                    if(m.getClass().equals(SudoProject.class)) {
                        // re-load list
                        mModelItemAdapter.changeDataSet(((SudoProject)m).getChildren());
                    } else {
                        // return the selected project.
                        Project p = (Project)m;
                        MainContext.getEventBus().post(new ChoseProjectEvent(p, ChooseProjectDialog.this));
                        // NOTE: the caller should close this dialog
                    }
                }
            });
        } else {
            dismiss();
        }

        return v;
    }

    /**
     * Specifies the model list to use in the dialog.
     * This must be called before showing the dialog.
     * @deprecated this was used by the p2p import before splitting into a different dialog.
     */
    public void setModels(Model[] models) {
        mModelList = models;
    }
}
