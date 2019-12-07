/**
 * Created by lwj on 2017/7/14.
 */
$(function () {
  $('#openMoreRouteBtn').on('click', function () {
    var $continaer =$('#commonUseRouteContainer')
      if($continaer.hasClass('opend')){
        $continaer.removeClass('opend')
      }else{
        $continaer.addClass('opend')
      }
  })
})