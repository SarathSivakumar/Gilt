package com.streak.gilt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailsFragment extends Fragment {

    TextView model,size,weight,factory,option,seal;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String modeltext,sizetext,weighttext,factorytext,optiontext,sealtext;
    public OrderDetailsFragment(String model,String size,String weight,String factory, String option, String seal) {
        modeltext=model;
        sizetext=size;
        weighttext=weight;
        factorytext=factory;
        optiontext=option;
        sealtext=seal;
    }

    public static OrderDetailsFragment newInstance(String param1, String param2,String param3, String param4,String param5, String param6) {
        OrderDetailsFragment fragment = new OrderDetailsFragment(param1,param2,param3,param4,param5,param6);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_order_details, container, false);
        model=view.findViewById(R.id.orderdetails_model);
        size=view.findViewById(R.id.orderdetails_size);
        weight=view.findViewById(R.id.orderdetails_weight);
        option=view.findViewById(R.id.orderdetails_option1);
        seal=view.findViewById(R.id.orderdetails_seal);
        factory=view.findViewById(R.id.orderdetails_factoryName);

        model.setText(modeltext);
        size.setText(sizetext);
        weight.setText(weighttext);
        option.setText(optiontext);
        seal.setText(sealtext);
        factory.setText(factorytext);
        return  view;
    }
}