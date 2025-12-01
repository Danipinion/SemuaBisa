import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R
import com.netown.semuabisa.features.train.TrainTicket
import com.netown.semuabisa.features.train.TrainTicketAdapter

class TrainSelectTicketFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrainTicketAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_train_select_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rvTrainList)

        val dummyData = listOf(
            TrainTicket("Train A | Economy", "MDN", "JKT", "20h 39m"),
            TrainTicket("Train A | Economy", "MDN", "JKT", "20h 39m"),
            TrainTicket("Train A | Economy", "MDN", "JKT", "20h 39m"),
            TrainTicket("Train A | Economy", "MDN", "JKT", "20h 39m"),
            TrainTicket("Train A | Economy", "MDN", "JKT", "20h 39m")
        )

        adapter = TrainTicketAdapter(dummyData) { ticket ->
            Toast.makeText(requireContext(), "Selected ${ticket.name}", Toast.LENGTH_SHORT).show()
            // TODO: navigate to seat selection
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}
