package io.airlift.resolver.internal;


import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

class SafeTransferListener extends AbstractTransferListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(io.airlift.resolver.internal.SafeTransferListener.class);
    private final TransferListener listener;

    public static TransferListener wrap(RepositorySystemSession session) {
        TransferListener listener = session.getTransferListener();
        return listener == null ? null : new io.airlift.resolver.internal.SafeTransferListener(listener);
    }

    protected SafeTransferListener(RepositorySystemSession session) {
        this(session.getTransferListener());
    }

    private SafeTransferListener(TransferListener listener) {
        this.listener = listener;
    }

    private void logError(TransferEvent event, Throwable e) {
        LOGGER.debug("Failed to dispatch transfer event '{}' to {}", new Object[]{event, this.listener.getClass().getCanonicalName(), e});
    }

    public void transferInitiated(TransferEvent event) throws TransferCancelledException {
        Objects.requireNonNull(event, "event cannot be null");
        if (this.listener != null) {
            try {
                this.listener.transferInitiated(event);
            } catch (LinkageError | RuntimeException var3) {
                Throwable e = var3;
                this.logError(event, e);
            }
        }

    }

    public void transferStarted(TransferEvent event) throws TransferCancelledException {
        Objects.requireNonNull(event, "event cannot be null");
        if (this.listener != null) {
            try {
                this.listener.transferStarted(event);
            } catch (LinkageError | RuntimeException var3) {
                Throwable e = var3;
                this.logError(event, e);
            }
        }

    }

    public void transferProgressed(TransferEvent event) throws TransferCancelledException {
        Objects.requireNonNull(event, "event cannot be null");
        if (this.listener != null) {
            try {
                this.listener.transferProgressed(event);
            } catch (LinkageError | RuntimeException var3) {
                Throwable e = var3;
                this.logError(event, e);
            }
        }

    }

    public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
        Objects.requireNonNull(event, "event cannot be null");
        if (this.listener != null) {
            try {
                this.listener.transferCorrupted(event);
            } catch (LinkageError | RuntimeException var3) {
                Throwable e = var3;
                this.logError(event, e);
            }
        }

    }

    public void transferSucceeded(TransferEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        if (this.listener != null) {
            try {
                this.listener.transferSucceeded(event);
            } catch (LinkageError | RuntimeException var3) {
                Throwable e = var3;
                this.logError(event, e);
            }
        }

    }

    public void transferFailed(TransferEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        if (this.listener != null) {
            try {
                this.listener.transferFailed(event);
            } catch (LinkageError | RuntimeException var3) {
                Throwable e = var3;
                this.logError(event, e);
            }
        }

    }
}
