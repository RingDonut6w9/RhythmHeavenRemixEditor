package io.github.chrislo27.rhre3.entity.model.multipart

import com.badlogic.gdx.math.Rectangle
import io.github.chrislo27.rhre3.entity.model.IRepitchable
import io.github.chrislo27.rhre3.entity.model.IStretchable
import io.github.chrislo27.rhre3.entity.model.MultipartEntity
import io.github.chrislo27.rhre3.registry.GameRegistry
import io.github.chrislo27.rhre3.registry.datamodel.impl.Pattern
import io.github.chrislo27.rhre3.track.Remix


class PatternEntity(remix: Remix, datamodel: Pattern)
    : MultipartEntity<Pattern>(remix, datamodel), IRepitchable, IStretchable {

    override var semitone: Int = 0
    override val canBeRepitched: Boolean by IRepitchable.anyInModel(datamodel)
    override val isStretchable: Boolean by IStretchable.anyInModel(datamodel)

    init {
        datamodel.cues.mapTo(internal) { pointer ->
            GameRegistry.data.objectMap[pointer.id]?.createEntity(remix)?.apply {
                this.bounds.x = this@PatternEntity.bounds.x + pointer.beat
                this.bounds.y = this@PatternEntity.bounds.y + pointer.track
                this.bounds.width = pointer.duration

                // apply cue pointer settings
                (this as? IRepitchable)?.semitone = pointer.semitone
            } ?: error("Object with id ${pointer.id} not found")
        }

        this.bounds.width = internal
                .maxBy { it.bounds.x + it.bounds.width }?.run { this.bounds.x + this.bounds.width - this@PatternEntity.bounds.x } ?:
                error("Nothing in internal cache")
    }

    override fun updateInternalCache(oldBounds: Rectangle) {
        translateInternal(oldBounds, changeWidths = true)
    }

}