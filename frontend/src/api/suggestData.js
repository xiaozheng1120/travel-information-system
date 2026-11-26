import request from '@/api/request'

/**
 * 搜索联想
 * @param {string} type spot | spotArea | guide | guideAuthor | hotel | ticket
 * @param {string} keyword 用户已输入的内容，可为空（为空时返回默认推荐）
 * @returns Promise<string[]>
 */
export function getSuggestApi(type, keyword) {
  return request.get('/api/suggest/list', {
    params: { type, keyword }
  })
}
