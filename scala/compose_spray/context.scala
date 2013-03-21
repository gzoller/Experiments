
import com.novus.salat._

package object context {
	val CustomTypeHint = "_type"
	implicit val ctx = new Context {
		val name = "JsonContext-As-Needed"
		override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = CustomTypeHint)
	}

	// Collection Names
	val COLLECTION_TAPES = "tapes"
}
