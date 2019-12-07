/**
 * Created by tcjr on 2017/7/18.
 */
$(function () {
  $("#deleteCheckedBtn").on('click',function () {
    deleteItems()
  })
})

function deleteItems() {
  var $flag = true
  var $items = $('#tableBody .table-list-checked_input:checked')
  if($items.length > 0){
    $items.each(function () {
      var $this = $(this);
      $this.closest('tr').remove()
    })
  }else {
    alert("至少选择一项")
  }
}