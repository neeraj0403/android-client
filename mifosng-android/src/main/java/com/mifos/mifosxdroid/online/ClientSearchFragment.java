package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.SearchedEntity;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClientSearchFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "Client Search Fragment";

    @InjectView(R.id.et_search_by_id)
    EditText et_searchById;
    @InjectView(R.id.bt_searchClient)
    Button bt_searchClient;
    @InjectView(R.id.lv_searchResults)
    ListView lv_searchResults;

    View rootView;
    List<String> clientNames = new ArrayList<String>();
    List<Integer> clientIds = new ArrayList<Integer>();
    SafeUIBlockingUtility safeUIBlockingUtility;
    private String searchQuery;


    public ClientSearchFragment() {
        // Required empty public constructor
    }

    public static ClientSearchFragment newInstance() {
        ClientSearchFragment fragment = new ClientSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_search, null);
        ButterKnife.inject(this, rootView);


        return rootView;
    }

    @OnClick(R.id.bt_searchClient)
    public void performSearch() {

        if (!et_searchById.getEditableText().toString().trim().isEmpty()) {
            searchQuery = et_searchById.getEditableText().toString().trim();
            findClients(searchQuery);

        } else {
            Toast.makeText(getActivity(), "No Search Query Entered!", Toast.LENGTH_SHORT).show();
        }


    }


    public void findClients(final String clientName) {

        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        API.searchService.searchClientsByName(clientName, new Callback<List<SearchedEntity>>() {
            @Override
            public void success(List<SearchedEntity> searchedEntities, Response response) {

                Iterator<SearchedEntity> iterator = searchedEntities.iterator();
                clientNames.clear();
                while (iterator.hasNext()) {
                    SearchedEntity searchedEntity = iterator.next();
                    clientNames.add("#" + searchedEntity.getEntityId() + " - " + searchedEntity.getEntityName());
                    clientIds.add(searchedEntity.getEntityId());
                }

                String[] clientNamesArrayForAdapter = clientNames.toArray(new String[clientNames.size()]);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, clientNamesArrayForAdapter);

                lv_searchResults.setAdapter(adapter);
                lv_searchResults.setOnItemClickListener(ClientSearchFragment.this);

                //If the search query returned one or more results close the keyboard

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(et_searchById.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                safeUIBlockingUtility.safelyUnBlockUI();


            }

            @Override
            public void failure(RetrofitError retrofitError) {
                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });


    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent clientActivityIntent = new Intent(getActivity(), ClientActivity.class);
        clientActivityIntent.putExtra(Constants.CLIENT_ID, clientIds.get(i));
        startActivity(clientActivityIntent);

    }


}
