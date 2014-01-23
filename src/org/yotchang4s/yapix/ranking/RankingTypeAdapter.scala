package org.yotchang4s.yapix.ranking

import android.view._
import android.widget._
import android.content.Context
import android.widget.CompoundButton.OnCheckedChangeListener
import android.os.Handler
import org.yotchang4s.yapix.R

class RankingTypeAdapter(spinner: Spinner) extends BaseAdapter {
  private[this] val inflater = spinner.getContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  private[this] var rankingLabels: List[RankingLabel] = Nil

  private[this] var dropdown = false

  def setRankingLabels(rankingLabels: List[RankingLabel]) {
    this.rankingLabels = if (rankingLabels != null) rankingLabels else Nil
  }

  def getRankingLabels: List[RankingLabel] = rankingLabels

  def getItem(position: Int) = rankingLabels(position)

  def getCount = rankingLabels.size

  // これでいいんだっけ？
  def getItemId(position: Int) = 31 + position.hashCode * 31 + rankingLabels(position).hashCode * 31

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val viewGroup = if (convertView == null) {
      inflater.inflate(R.layout.ranking_type_spinner_dropdown_item, null, false)
    } else {
      convertView
    }
    val viewHolder =
      if (convertView == null) {
        val textView = viewGroup.findViewById(R.id.rankingTypeSpinnterDropDownText).asInstanceOf[TextView]
        val switchView = viewGroup.findViewById(R.id.rankingTypeSpinnterDropDownR18Switch).asInstanceOf[CompoundButton]

        val vh = ViewHolder(viewGroup, textView, switchView)
        viewGroup.setTag(vh)
        vh
      } else {
        viewGroup.getTag.asInstanceOf[ViewHolder]
      }

    val rankingLabel = rankingLabels(position)
    viewHolder.switchView.setOnCheckedChangeListener(new OnCheckedChangeListener {
      var d = dropdown
      var ps = position
      var checkChanged = false
      def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val newRankingLabels = for ((l, i) <- rankingLabels.zipWithIndex) yield {
          i match {
            case p if (p == ps) =>
              val rl = RankingLabel(l.text, l.existR18, isChecked)
              if (l.r18 != isChecked) { checkChanged = true }
              rl
            case _ => l
          }
        }
        if (!checkChanged) {
          return
        }
        rankingLabels = newRankingLabels

        if (!d && getItemId(spinner.getSelectedItemPosition) == getItemId(ps)) {
          Option(spinner.getOnItemSelectedListener) foreach (_.onItemSelected(spinner, buttonView, position, getItemId(ps)))
        }
      }
    })

    viewHolder.switchView.setVisibility(if (rankingLabel.existR18) View.VISIBLE else View.GONE)
    viewHolder.switchView.setChecked(rankingLabel.r18)
    viewHolder.textView.setText(rankingLabel.text)

    dropdown = false

    viewGroup
  }

  override def getDropDownView(position: Int, convertView: View, parent: ViewGroup): View = {
    dropdown = true
    getView(position, convertView, parent)
  }
}
private[this] case class ViewHolder(viewGroup: View, textView: TextView, switchView: CompoundButton)

case class RankingLabel(text: String, existR18: Boolean, r18: Boolean = false)
