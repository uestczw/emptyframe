/**
 * Created by tcjr on 2017/7/21.
 */
(function ($) {
  $.fn.extend({
    uploadPreview: function (opts) {
      var _self = this,
        _this = $(this);
      opts = jQuery.extend({
        Img: "ImgPr",
        Width: 100,
        Height: 100,
        ImgType: ["gif", "jpeg", "jpg", "bmp", "png"],
        Callback: function () {}
      }, opts || {});
      _self.getObjectURL = function (file) {
        var url = null;
        if (window.createObjectURL != undefined) {
          url = window.createObjectURL(file)
        } else if (window.URL != undefined) {
          url = window.URL.createObjectURL(file)
        } else if (window.webkitURL != undefined) {
          url = window.webkitURL.createObjectURL(file)
        }
        return url
      };
      _this.change(function () {
        if (this.value) {
          if (!RegExp("\.(" + opts.ImgType.join("|") + ")$", "i").test(this.value.toLowerCase())) {
            alert("选择文件错误,图片类型必须是" + opts.ImgType.join("，") + "中的一种");
            this.value = "";
            return false
          }
          _this.closest('.certificate-card_container').find('.certificate-card_img').css({'background-image': 'url('+_self.getObjectURL(this.files[0])+')'})
          opts.Callback()
        }
      })
    }
  });
})(jQuery)

