package pl.skyterix.sadsky.pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

import static pl.skyterix.sadsky.pageable.PageableConstants.DEFAULT_PAGE;
import static pl.skyterix.sadsky.pageable.PageableConstants.DEFAULT_PAGE_SIZE;
import static pl.skyterix.sadsky.pageable.PageableConstants.DEFAULT_SORT;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageableRequest implements Serializable {

    private String sort;

    private String order;

    private Integer size;

    private Integer page;

    public Pageable toPageable() {
        if (size == null) size = DEFAULT_PAGE_SIZE;
        if (page == null) page = DEFAULT_PAGE;
        if (sort == null) sort = DEFAULT_SORT;
        if (size > PageableConstants.MAX_PAGE_SIZE) size = PageableConstants.MAX_PAGE_SIZE;

        Sort.Direction sortDirection = Sort.Direction.fromOptionalString(order)
                .orElse(Sort.Direction.DESC);

        // Start counting pages from one
        return PageRequest.of(--page, size, Sort.by(sortDirection, sort));
    }
}
