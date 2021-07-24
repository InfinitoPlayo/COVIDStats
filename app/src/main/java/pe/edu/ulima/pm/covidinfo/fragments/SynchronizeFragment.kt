package pe.edu.ulima.pm.covidinfo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import pe.edu.ulima.pm.covidinfo.R

class SynchronizeFragment: Fragment() {

    private var butSynchronize: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_synchronize, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Cambiar de fragment al sincronizar
        butSynchronize = view.findViewById(R.id.butSynchronize)
        butSynchronize!!.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.flaMain, GlobalInfoFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }
    }
}