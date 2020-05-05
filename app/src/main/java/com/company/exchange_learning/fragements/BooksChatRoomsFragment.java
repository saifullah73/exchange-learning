package com.company.exchange_learning.fragements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.company.exchange_learning.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksChatRoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksChatRoomsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BooksChatRoomsFragment() {
        // Required empty public constructor
    }

    public static BooksChatRoomsFragment newInstance(String param1, String param2) {
        BooksChatRoomsFragment fragment = new BooksChatRoomsFragment();
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
        return inflater.inflate(R.layout.fragment_books_chat_rooms, container, false);
    }
}
