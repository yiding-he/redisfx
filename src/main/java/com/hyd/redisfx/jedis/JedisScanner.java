package com.hyd.redisfx.jedis;

import com.hyd.fx.utils.Nullable;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JedisScanner {

    @FunctionalInterface
    public interface CursorIterator {

        ScanResult<String> iterate(String cursor);
    }

    public static List<String> scan(CursorIterator operation, int from, int to) {
        return scan(operation, from, to, null);
    }

    public static List<String> scan(
        CursorIterator operation, int from, int to,
        @Nullable Supplier<Boolean> canceller
    ) {
        int limit = to - from;
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanResult<String> scanResult;
        List<String> resultList = new ArrayList<>();

        do {
            if (canceller != null && canceller.get()) {
                return resultList;
            }
            scanResult = operation.iterate(cursor);
            cursor = scanResult.getCursor();
            limit -= scanResult.getResult().size();
            resultList.addAll(scanResult.getResult());
        } while (!scanResult.isCompleteIteration() && limit > 0);

        return resultList;
    }
}
