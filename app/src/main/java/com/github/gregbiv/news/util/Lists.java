
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.util;

import java.util.Collection;

public final class Lists {
    public static <E> boolean isEmpty(Collection<E> list) {
        return ((list == null) || (list.size() == 0));
    }
}
