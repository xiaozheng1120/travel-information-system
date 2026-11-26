// uploadData.js
// 通用图片上传接口
import request from './request'

/**
 * 上传图片到后端
 * @param {File} file 原始文件对象（不是 Object URL）
 * @returns {Promise<{code:number, msg:string, url:string}>}
 */
export function uploadImageApi(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/upload/image', formData, {
    timeout: 30000,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
