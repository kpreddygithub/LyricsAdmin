package org.kprsongs.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.kprsongs.service.CommonService;
import org.kprsongs.admin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: K Purushotham Reddy
 * version :1.0.0
 */
public abstract class AddPlayListsDialogFragment extends DialogFragment
{

    private CommonService commonService = new CommonService();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.add_service_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        alertDialogBuilder.setView(promptsView);
        final EditText serviceName = (EditText) promptsView.findViewById(R.id.service_name);
        alertDialogBuilder.setTitle("Enter the favourite name:");

        alertDialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                String service_name;
                if (serviceName.getText().toString().equals(""))
                    Toast.makeText(getActivity(), "Enter favourite name...!", Toast.LENGTH_LONG).show();
                else {
                    service_name = serviceName.getText().toString();
                    List<String> serviceNames = new ArrayList<String>();
                    serviceNames.addAll(commonService.readServiceName());
                    //adapterService.setServiceNames(serviceNames);
                    commonService.saveIntoFile(service_name, getSelectedSong().toString());
                    Toast.makeText(getActivity(), "Song added to favourite...!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    SongsListFragment listFragment = new SongsListFragment();
                    listFragment.onRefresh();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(getResources().getColor(R.color.accent_material_light));
            }
        });
        return alertDialog;
    }

    public abstract String getSelectedSong();

}
