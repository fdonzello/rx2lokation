package io.fraway.android.libs.widgets

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.ArrayAdapter
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.fraway.android.libs.RxLocationProvider
import io.fraway.android.libs.models.RichLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class PlaceAutocompleteTextView : AppCompatAutoCompleteTextView {

    interface Listener {
        fun onPlaceChosen(richLocation: RichLocation)
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private lateinit var rxLocationProvider: RxLocationProvider

    lateinit var selectedLocation: RichLocation

    private var lastLocations: List<RichLocation>? = null

    var listener: Listener? = null

    private var skipAutocomplete: Boolean = false

    fun setTextWithoutAutocomplete(text: String) {
        skipAutocomplete = true
        setText(text)
        clearFocus()
    }

    private fun getActivityLoopingOnContext(): Activity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    private var subscriber: Disposable? = null

    private fun init() {
        this.rxLocationProvider = RxLocationProvider(activity = getActivityLoopingOnContext()!!)

        setOnItemClickListener({ adapterView, _, i, _ ->
            selectedLocation = adapterView.getItemAtPosition(i) as RichLocation
            listener?.onPlaceChosen(selectedLocation)
        })

        subscriber = RxTextView.afterTextChangeEvents(this)
                .observeOn(AndroidSchedulers.mainThread())
                .map { e -> e.editable()!!.toString() }
                .skip(1)
                .filter({ text ->
                    if (skipAutocomplete) {
                        skipAutocomplete = false
                        return@filter false
                    }

                    Timber.i("filtering %s", text)
                    with(lastLocations) {
                        if (this == null || this.isEmpty()) {
                            return@filter true
                        }

                        for (lastLocation in this) {
                            if (lastLocation.address == text) {
                                return@filter false
                            }
                        }

                        true
                    }

                })
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS)
                .switchMap<List<RichLocation>> { query ->
                    rxLocationProvider
                            .autocompletePlace(query)
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe(
                        { locations ->
                            this.lastLocations = locations
                            setAdapter(LocationSuggestionAdapter(
                                    context,
                                    android.R.layout.simple_list_item_1,
                                    locations.toMutableList()
                            ))
                            showDropDown()
                        }
                ) { err -> Timber.e(err) }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(outAttrs)
        if (ic != null && outAttrs.hintText == null) {
            // If we don't have a hint and our parent is a TextInputLayout, use it's hint for the
            // EditorInfo. This allows us to display a hint in 'extract mode'.
            val parent = parent
            if (parent is TextInputLayout) {
                outAttrs.hintText = parent.hint
            }
        }
        return ic
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        subscriber?.dispose()
    }

    class LocationSuggestionAdapter(context: Context?, resource: Int, objects: MutableList<RichLocation>?)
        : ArrayAdapter<RichLocation>(context, android.R.layout.simple_list_item_1, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View? = convertView

            if (view == null) {
                view = LayoutInflater.from(parent!!.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            }

            val text: TextView? = view?.findViewById(android.R.id.text1)
            text?.text = getItem(position).address

            return view!!
        }
    }
}